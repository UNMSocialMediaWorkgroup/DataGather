package com.accelerama.compression;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * A small {@link java.io.InputStream} wrapper which reads larger data types
 * as a series of bytes. These data types are guaranteed to be read correctly
 * is they are written by a {@link com.lunagameserve.compression.ByteWriter}.
 *
 * @author Six
 * @since March 2, 2015
 * @see com.lunagameserve.compression.ByteWriter
 */
public class ByteReader {

    /**
     * The underlying {@link java.io.InputStream} which all bytes will be read
     * from.
     */
    private final InputStream in;

    /**
     * Constructs a new {@link com.lunagameserve.compression.ByteReader} which
     * will read bytes from a specified {@link java.io.InputStream}.
     *
     * @param in The {@link java.io.InputStream} which all bytes will be read
     *           from.
     */
    public ByteReader(InputStream in) {
        this.in = in;
    }

    /**
     * Reads a {@code float} from the next four bytes of the underlying
     * {@link java.io.InputStream}.
     *
     * @return The {@code float} represented by the next four bytes of the
     *         underling {@link java.io.InputStream}.
     *
     * @throws IOException If the underlying {@link java.io.InputStream} cannot
     *                     be read from for any reason.
     */
    public float readFloat() throws IOException {
        return ByteBuffer.wrap(readBytes(4)).getFloat();
    }

    /**
     * Reads a {@code long} from the next four bytes of the underlying
     * {@link java.io.InputStream}.
     *
     * @return The {@code long} represented by the next eight bytes of the
     *         underling {@link java.io.InputStream}.
     *
     * @throws IOException If the underlying {@link java.io.InputStream} cannot
     *                     be read from for any reason.
     */
    public long readLong() throws IOException {
        return ByteBuffer.wrap(readBytes(8)).getLong();
    }

    /**
     * Reads a {@code int} from the next four bytes of the underlying
     * {@link java.io.InputStream}.
     *
     * @return The {@code int} represented by the next four bytes of the
     *         underling {@link java.io.InputStream}.
     *
     * @throws IOException If the underlying {@link java.io.InputStream} cannot
     *                     be read from for any reason.
     */
    public int readInt() throws IOException {
        return ByteBuffer.wrap(readBytes(4)).getInt();
    }

    /**
     * Reads a {@code short} from the next two bytes of the underlying
     * {@link java.io.InputStream}.
     *
     * @return The {@code short} represented by the next two bytes of the
     *         underling {@link java.io.InputStream}.
     *
     * @throws IOException If the underlying {@link java.io.InputStream} cannot
     *                     be read from for any reason.
     */
    public int readShort() throws IOException {
        return ByteBuffer.wrap(readBytes(2)).getShort();
    }

    /**
     * Reads a {@code byte[]} from the next specified number of bytes from the
     * underlying {@link java.io.InputStream}.
     *
     * @param count The number of {@code byte}s which should be read from the
     *              underlying {@link java.io.InputStream}.
     *
     * @return The {@code byte[]} represented by the next four bytes of the
     *         underling {@link java.io.InputStream}.
     *
     * @throws IOException If the underlying {@link java.io.InputStream} cannot
     *                     be read from for any reason.
     */
    private byte[] readBytes(int count) throws IOException {
        byte[] bytes = new byte[count];
        for (int i = 0; i < count; i++) {
            bytes[i] = (byte)in.read();
        }
        return bytes;
    }

    /**
     * Closes this {@link com.lunagameserve.compression.ByteReader}, also
     * closing its held {@link java.io.InputStream}.
     *
     * @throws IOException If the underlying {@link java.io.InputStream}
     *                     cannot be closed for any reason.
     */
    public void close() throws IOException {
        in.close();
    }
}
