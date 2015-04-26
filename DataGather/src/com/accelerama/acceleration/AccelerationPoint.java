package com.accelerama.acceleration;

import com.accelerama.compression.ByteReader;
import com.accelerama.compression.ByteWriter;
import com.lunagameserve.nbt.NBTException;
import com.lunagameserve.nbt.NBTSerializableObject;
import com.lunagameserve.nbt.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Ross on 2/27/2015.
 */
public class AccelerationPoint {
    private float[] values;
    private long timestamp;

    public static final int SIZE = 20;

    public AccelerationPoint() { }

    public AccelerationPoint(float[] values, long timestamp) {
        this.timestamp = timestamp;
        this.values = new float[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
    }

    public float getX() {
        return values[0];
    }

    public float getY() {
        return values[1];
    }

    public float getZ() {
        return values[2];
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Tag.Compound toCompound() throws NBTException {
        return new Tag.Compound.Builder()
                .addFloat("x", values[0])
                .addFloat("y", values[1])
                .addFloat("z", values[2])
                .addLong("t", timestamp)
                .toCompound("accelerationPoint");
    }

    public void writeAsBytes(OutputStream out) throws IOException {
        ByteWriter writer = new ByteWriter(out);
        writer.writeFloat(values[0]);
        writer.writeFloat(values[1]);
        writer.writeFloat(values[2]);
        writer.writeLong(timestamp);
    }

    public void readFromBytes(InputStream in) throws IOException {
        ByteReader reader = new ByteReader(in);
        values = new float[3];
        values[0] = reader.readFloat();
        values[1] = reader.readFloat();
        values[2] = reader.readFloat();
        timestamp = reader.readLong();
    }

    public boolean valid() {
        double modulus = Math.sqrt(values[0] * values[0] +
                                   values[1] * values[1] +
                                   values[2] * values[2]);
        return modulus < 1000;
    }

    public void fromCompound(Tag.Compound compound) {
        this.values[0] = compound.getFloat("x");
        this.values[1] = compound.getFloat("y");
        this.values[2] = compound.getFloat("z");
        this.timestamp = compound.getLong("t");
    }
}
