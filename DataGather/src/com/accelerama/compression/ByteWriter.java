package com.accelerama.compression;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * A small {@link java.io.OutputStream} wrapper which writes larger data types
 * as a series of bytes. These data types are guaranteed to be read correctly
 * by a {@link com.lunagameserve.compression.ByteReader}.
 *
 * @author Six
 * @since March 2, 2015
 * @see com.lunagameserve.compression.ByteReader
 */
public class ByteWriter {

    /**
     * The underlying {@link java.io.OutputStream} which all bytes will be
     * written to.
     */
    private final OutputStream out;

    /**
     * Constructs a new {@link com.lunagameserve.compression.ByteWriter} which
     * will write bytes to a specified {@link java.io.OutputStream}.
     *
     * @param out The {@link java.io.OutputStream} which all bytes will be read
     *            from.
     */
    public ByteWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Writes a {@code float} as the next four bytes of the underlying
     * {@link java.io.OutputStream}.
     *
     * @param f The {@code float} to be written as the next four bytes of the
     *          underling {@link java.io.OutputStream}.
     *
     * @throws IOException If the underlying {@link java.io.OutputStream} cannot
     *                     be written to for any reason.
     */
    public void writeFloat(float f) throws IOException {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putFloat(f);
        out.write(bytes);
    }

    /**
     * Writes a {@code long} as the next eight bytes of the underlying
     * {@link java.io.OutputStream}.
     *
     * @param l The {@code long} to be written as the next eight bytes of the
     *          underling {@link java.io.OutputStream}.
     *
     * @throws IOException If the underlying {@link java.io.OutputStream} cannot
     *                     be written to for any reason.
     */
    public void writeLong(long l) throws IOException {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(l);
        out.write(bytes);
    }

    /**
     * Writes a {@code int} as the next four bytes of the underlying
     * {@link java.io.OutputStream}.
     *
     * @param i The {@code int} to be written as the next four bytes of the
     *          underling {@link java.io.OutputStream}.
     *
     * @throws IOException If the underlying {@link java.io.OutputStream} cannot
     *                     be written to for any reason.
     */
    public void writeInt(int i) throws IOException {
        byte[] bytes = new byte[4];
        ByteBuffer.wrap(bytes).putInt(i);
        out.write(bytes);
    }

    /**
     * Writes a {@code short} as the next two bytes of the underlying
     * {@link java.io.OutputStream}.
     *
     * @param s The {@code short} to be written as the next two bytes of the
     *          underling {@link java.io.OutputStream}.
     *
     * @throws IOException If the underlying {@link java.io.OutputStream} cannot
     *                     be written to for any reason.
     */
    public void writeShort(short s) throws IOException {
        byte[] bytes = new byte[2];
        ByteBuffer.wrap(bytes).putShort(s);
        out.write(bytes);
    }

    /**
     * Closes this {@link com.lunagameserve.compression.ByteWriter}, also
     * closing its held {@link java.io.OutputStream}.
     *
     * @throws IOException If the underlying {@link java.io.OutputStream}
     *                     cannot be closed for any reason.
     */
    public void close() throws IOException {
        out.close();
    }
}
