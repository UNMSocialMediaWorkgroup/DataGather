package com.accelerama.acceleration;

import com.accelerama.compression.ByteReader;
import com.accelerama.compression.ByteWriter;
import com.lunagameserve.nbt.NBTException;
import com.lunagameserve.nbt.NBTSerializableListAdapter;
import com.lunagameserve.nbt.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * A collection of {@link com.accelerama.acceleration.AccelerationPoint}s
 * which provides some useful accessor methods. Note that this class does not
 * extend {@link java.util.Collection}.
 *
 * @author Six
 * @since March 2, 2015
 */
public class AccelerationCollection{

    /**
     * The {@link com.accelerama.nbt.Tag.List} of
     * {@link com.accelerama.acceleration.AccelerationPoint}s which this
     * {@link com.accelerama.acceleration.AccelerationCollection}
     * is currently tracking.
     */
    private ArrayList<AccelerationPoint> points =
            new ArrayList<AccelerationPoint>();

    /**
     * The size, in bytes, of all useful serializable data included in this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     *
     * @return The size, in bytes, of all useful serializable data included
     *         in this
     *            {@link com.accelerama.acceleration.AccelerationCollection}.
     */
    public int byteSize() {
        return points.size() * AccelerationPoint.SIZE;
    }

    /**
     * Adds an {@link com.accelerama.acceleration.AccelerationPoint} to this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     *
     * @param pt The {@link com.accelerama.acceleration.AccelerationPoint} to
     *           add.
     */
    public void addPoint(AccelerationPoint pt) {
        this.points.add(pt);
    }

    /**
     * Gets an {@link com.accelerama.acceleration.AccelerationPoint} from
     * this collection, given its index.
     *
     * @param i The index of the
     *          {@link com.accelerama.acceleration.AccelerationPoint} to
     *          retrieve.
     *
     * @return the {@link com.accelerama.acceleration.AccelerationPoint} from
     * this collection at index {@code i}.
     *
     * @throws java.lang.IndexOutOfBoundsException if {@code index} is out of
     *                                             the bounds of the size of
     *                                             this collection.
     */
    public AccelerationPoint get(int i) {
        return points.get(i);
    }

    /**
     * The maximum value of a data point on the x-axis of all
     * {@link com.accelerama.acceleration.AccelerationPoint}s in this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     *
     * @return The maximum value of a data point on the x-axis of all
     *         {@link com.accelerama.acceleration.AccelerationPoint}s in this
     *         {@link com.accelerama.acceleration.AccelerationCollection}.
     */
    public float maxX() {
        float max = points.get(0).getX();
        for (AccelerationPoint p : points) {
            if (p.getX() > max) {
                max = p.getX();
            }
        }
        return max;
    }

    /**
     * The minimum value of a data point on the x-axis of all
     * {@link com.accelerama.acceleration.AccelerationPoint}s in this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     *
     * @return The minimum value of a data point on the x-axis of all
     *         {@link com.accelerama.acceleration.AccelerationPoint}s in this
     *         {@link com.accelerama.acceleration.AccelerationCollection}.
     */
    public float minX() {
        float min = points.get(0).getX();
        for (AccelerationPoint p : points) {
            if (p.getX() < min) {
                min = p.getX();
            }
        }
        return min;
    }

    /**
     * The maximum value of a data point on the y-axis of all
     * {@link com.accelerama.acceleration.AccelerationPoint}s in this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     *
     * @return The maximum value of a data point on the y-axis of all
     *         {@link com.accelerama.acceleration.AccelerationPoint}s in this
     *         {@link com.accelerama.acceleration.AccelerationCollection}.
     */
    public float maxY() {
        float max = points.get(0).getY();
        for (AccelerationPoint p : points) {
            if (p.getY() > max) {
                max = p.getY();
            }
        }
        return max;
    }

    /**
     * The minimum value of a data point on the y-axis of all
     * {@link com.accelerama.acceleration.AccelerationPoint}s in this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     *
     * @return The minimum value of a data point on the y-axis of all
     *         {@link com.accelerama.acceleration.AccelerationPoint}s in this
     *         {@link com.accelerama.acceleration.AccelerationCollection}.
     */
    public float minY() {
        float min = points.get(0).getY();
        for (AccelerationPoint p : points) {
            if (p.getY() < min) {
                min = p.getY();
            }
        }
        return min;
    }

    /**
     * The maximum value of a data point on the z-axis of all
     * {@link com.accelerama.acceleration.AccelerationPoint}s in this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     *
     * @return The maximum value of a data point on the z-axis of all
     *         {@link com.accelerama.acceleration.AccelerationPoint}s in this
     *         {@link com.accelerama.acceleration.AccelerationCollection}.
     */
    public float maxZ() {
        float max = points.get(0).getZ();
        for (AccelerationPoint p : points) {
            if (p.getZ() > max) {
                max = p.getZ();
            }
        }
        return max;
    }

    /**
     * The minimum value of a data point on the z-axis of all
     * {@link com.accelerama.acceleration.AccelerationPoint}s in this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     *
     * @return The minimum value of a data point on the z-axis of all
     *         {@link com.accelerama.acceleration.AccelerationPoint}s in this
     *         {@link com.accelerama.acceleration.AccelerationCollection}.
     */
    public float minZ() {
        float min = points.get(0).getZ();
        for (AccelerationPoint p : points) {
            if (p.getZ() < min) {
                min = p.getZ();
            }
        }
        return min;
    }

    /**
     * Gets the number of
     * {@link com.accelerama.acceleration.AccelerationPoint}s held by this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     * @return The number of
     * {@link com.accelerama.acceleration.AccelerationPoint}s held by this
     * {@link com.accelerama.acceleration.AccelerationCollection}.
     */
    public int size() {
        return points.size();
    }

//    /** {@inheritDoc} */
//    @Override
//    protected Tag listItemToTag(int i) {
//        try {
//            return points.get(i).toCompound();
//        } catch (NBTException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
    
//    /** {@inheritDoc} */
//    @Override
//    protected void listItemFromTag(int i, Tag tag) {
//        AccelerationPoint pt = new AccelerationPoint();
//        pt.fromCompound((Tag.Compound)tag);
//        points.add(pt);
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    protected int listItemCount() {
//        return points.size();
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    protected String listName() {
//        return "accelerationCollection";
//    }

    /**
     * Writes an output sequence, which can be
     * {@link #readFromBytes(java.io.InputStream)}, to a specified
     * {@link java.io.OutputStream}, essentially saving all held
     * {@link com.accelerama.acceleration.AccelerationPoint}s.
     *
     * @param out The {@link java.io.OutputStream} to write to. It is the
     *            caller's responsibility to close this when necessary.
     *
     * @throws IOException if {@code out} cannot be written to for any reason.
     */
    public void writeAsBytes(OutputStream out) throws IOException {
        ByteWriter writer = new ByteWriter(out);
        writer.writeInt(size());
        for (int i = 0; i < size(); i++) {
            points.get(i).writeAsBytes(out);
        }
    }

    /**
     * Reads an input sequence, which should have been written by
     * {@link #writeAsBytes(java.io.OutputStream)}, from a specified
     * {@link java.io.InputStream}. This
     * {@link com.accelerama.acceleration.AccelerationCollection} is first
     * {@link #clear()}ed, and after this call will contain only the
     * {@link com.accelerama.acceleration.AccelerationPoint}s represented
     * by what is held in the specified {@link java.io.InputStream}.
     *
     * @param in The {@link java.io.InputStream} to read from. It is the
     *            caller's responsibility to close this when necessary.
     *
     * @throws IOException if {@code in} cannot be read from for any reason.
     */
    public void readFromBytes(InputStream in) throws IOException {
        ByteReader reader = new ByteReader(in);
        points.clear();
        int size = reader.readInt();
        for (int i = 0; i < size; i++) {
            AccelerationPoint pt = new AccelerationPoint();
            pt.readFromBytes(in);
            addPoint(pt);
        }
    }

    /**
     * Stops tracking all currently tracked
     * {@link com.accelerama.acceleration.AccelerationPoint}s, essentially
     * making this
     * {@link com.accelerama.acceleration.AccelerationCollection} empty.
     */
    public void clear() {
        this.points.clear();
    }
}
