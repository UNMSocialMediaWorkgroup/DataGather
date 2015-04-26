package com.accelerama.light;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.accelerama.acceleration.AccelerationCollection;
import com.accelerama.compression.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by sixstring982 on 4/8/15.
 */
public enum LightCompressor {
    Uncompressed {
        @Override
        public void write(OutputStream out, ArrayList<Float> collection)
                throws IOException {
            ByteWriter writer = new ByteWriter(out);
            for (Float f : collection) {
                writer.writeFloat(f);
            }
            writer.close();
        }

        @Override
        public void read(InputStream in) throws IOException {
            super.read(in);
            ByteReader reader = new ByteReader(in);
            while (in.available() > 0) {
                lightPoints.add(reader.readFloat());
            }
            in.close();
        }
    },
    StandardGZip {
        @Override
        public void write(OutputStream out, ArrayList<Float> collection)
                throws IOException {
            GZIPOutputStream gz = new GZIPOutputStream(out);
            ByteWriter writer = new ByteWriter(gz);
            for (Float f : collection) {
                writer.writeFloat(f);
            }
            writer.close();
        }

        @Override
        public void read(InputStream in) throws IOException {
            super.read(in);
            GZIPInputStream gz = new GZIPInputStream(in);
            ByteReader reader = new ByteReader(gz);
            while (gz.available() > 0) {
                lightPoints.add(reader.readFloat());
            }
            reader.close();
        }
    },
    NybbleDownsampling {

        private final int NYBBLE_SIZE = 3;

        @Override
        public void write(OutputStream out, ArrayList<Float> collection)
                throws IOException {
            BitWriter bwrite = new BitWriter(out);
            ByteWriter writer = new ByteWriter(bwrite);

            float min = minPoint(collection);
            float max = maxPoint(collection);
            float binSize =
                    /* Need to add 0.001f for weird lossy rounding error,
                     * shouldn't make a difference */
                    (float)((max - min) / Math.pow(2, NYBBLE_SIZE)) + 0.001f;

            writer.writeFloat(min);
            writer.writeFloat(max);

            for (Float f : collection) {
                int val = (int)((f - min) / binSize);
                bwrite.writeBits(val, NYBBLE_SIZE);

            }
            writer.close();
        }

        @Override
        public void read(InputStream in) throws IOException {
            super.read(in);
            BitReader bread = new BitReader(in);
            ByteReader reader = new ByteReader(bread);

            float min = reader.readFloat();
            float max = reader.readFloat();
            float binSize = (float)((max - min) / Math.pow(2, NYBBLE_SIZE));

            while (in.available() > 0) {
                int bin = bread.readBits(NYBBLE_SIZE);
                lightPoints.add((bin * binSize) + min);
            }

            reader.close();
        }
    },
    NybbleDownsamplingGZIP {

        private final int NYBBLE_SIZE = 3;

        @Override
        public void write(OutputStream out, ArrayList<Float> collection)
                throws IOException {
            GZIPOutputStream gz = new GZIPOutputStream(out);
            BitWriter bwrite = new BitWriter(gz);
            ByteWriter writer = new ByteWriter(bwrite);

            float min = minPoint(collection);
            float max = maxPoint(collection);
            float binSize =
                    /* Need to add 0.001f for weird lossy rounding error,
                     * shouldn't make a difference */
                    (float)((max - min) / Math.pow(2, NYBBLE_SIZE)) + 0.001f;

            writer.writeFloat(min);
            writer.writeFloat(max);

            for (Float f : collection) {
                int val = (int)((f - min) / binSize);
                bwrite.writeBits(val, NYBBLE_SIZE);

            }
            writer.close();
        }

        @Override
        public void read(InputStream in) throws IOException {
            super.read(in);
            GZIPInputStream gz = new GZIPInputStream(in);
            BitReader bread = new BitReader(gz);
            ByteReader reader = new ByteReader(bread);

            float min = reader.readFloat();
            float max = reader.readFloat();
            float binSize = (float)((max - min) / Math.pow(2, NYBBLE_SIZE));

            while (gz.available() > 0) {
                int bin = bread.readBits(NYBBLE_SIZE);
                lightPoints.add((bin * binSize) + min);
            }

            reader.close();
        }
    };

    protected ArrayList<Float> lightPoints = new ArrayList<Float>();

    public abstract void write(OutputStream out,
                               ArrayList<Float> collection) throws IOException;

    public void read(InputStream in) throws IOException {
        lightPoints.clear();
    }

    public StreamStats ratio(ArrayList<Float> points) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            write(out, points);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] bytes = out.toByteArray();

        return new StreamStats(
                (double)bytes.length / (points.size() * 4), bytes.length);
    }

    protected float maxPoint(ArrayList<Float> points) {
        float max = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            max = Math.max(points.get(i), max);
        }
        return max;
    }

    protected float minPoint(ArrayList<Float> points) {
        float min = points.get(0);
        for (int i = 1; i < points.size(); i++) {
            min = Math.min(points.get(i), min);
        }
        return min;
    }

    protected float heightDelta(ArrayList<Float> points) {
        return maxPoint(points) - minPoint(points);
    }



    public void render(Canvas c, int color) {

        float dx = ((float)c.getWidth()) / ((float)lightPoints.size());
        float dy = ((float) c.getHeight()) / (heightDelta(lightPoints));
        float sy = minPoint(lightPoints);

        Paint paint = new Paint();
        paint.setColor(color);

        Paint axisPaint = new Paint();
        axisPaint.setColor(Color.GRAY);
        c.drawLine(0, (0f - sy) * dy, c.getWidth(), (0f - sy) * dy, axisPaint);

        for (int i = 1; i < lightPoints.size(); i++) {
            float x1 = (i - 1) * dx;
            float x2 = i * dx;
            float y1 = (lightPoints.get(i - 1) - sy) * dy;
            float y2 = (lightPoints.get(i) - sy) * dy;

            c.drawLine(x1,c.getHeight() - y1, x2, c.getHeight() - y2, paint);
        }
    }
}
