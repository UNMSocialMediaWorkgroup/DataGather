package com.accelerama.compression;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An IO Construct used to writes groups of bits to an
 * {@link java.io.OutputStream}.
 * <p>
 *     <b><i>Note:</i></b>
 *     This stream is far slower than other
 *     {@link java.io.OutputStream}s, and really should only be used when
 *     needing IO on the bit (not byte) level.
 * </p>
 *
 * @author Six
 * @since March 2, 2015
 * @see com.lunagameserve.compression.BitReader
 */
public class BitWriter extends OutputStream {

    /**
     * The current byte which has technically been written to, but has not yet
     * been written to the underlying {@link java.io.OutputStream}.
     */
    private int currentByte = 0;

    /**
     * The position of the bit in {@link #currentByte} which will be written
     * to next.
     */
    private int currentBytePos = 0;

    /**
     * The underlying {@link java.io.OutputStream} that this
     * {@link com.lunagameserve.compression.BitWriter} writes all of its input
     * to.
     */
    private final OutputStream out;

    /**
     * Constructs a new {@link com.lunagameserve.compression.BitWriter} backed
     * by a specified {@link java.io.OutputStream} ready for bit-level IO.
     *
     * @param out The {@link java.io.OutputStream} that this
     *            {@link com.lunagameserve.compression.BitWriter} will write
     *            all of its input to.
     */
    public BitWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Writes a single bit, specified by the LSB of a specified {@code int}, to
     * this {@link com.lunagameserve.compression.BitWriter}.
     *
     * @param bit The {@code int} containing the bit to write to this
     *            {@link com.lunagameserve.compression.BitWriter}. Only the
     *            LSB of this {@code int} will be read.
     *
     * @throws IOException If the internal {@link java.io.OutputStream} cannot
     *                     be written to for any reason.
     */
    public void writeBit(int bit) throws IOException {
        bit &= 1;

        currentByte |= (bit << currentBytePos);

        currentBytePos = (currentBytePos + 1) % 8;
        if (currentBytePos == 0) {
            out.write(currentByte);
            currentByte = 0;
        }
    }

    public void writeBit(boolean bit) throws IOException {
        writeBit(bit ? 1 : 0);
    }

    public void writeOne() throws IOException {
        writeBit(1);
    }

    public void writeZero() throws IOException {
        writeBit(0);
    }
    /**
     * Writes a specified number of bits, specified by the LSBs of a specified
     * {@code int}.
     *
     * @param word The {@code int} containing the bits to write. These will be
     *             written from the LSB end, with the LSB written first.
     *
     * @param bits The number of bits from {@code word}'s LSBs to write.
     *
     * @throws IOException If the underlying {@link java.io.OutputStream} cannot
     *                     be written to for any reason.
     */
    public void writeBits(int word, int bits) throws IOException {
        for (int i = 0; i < bits; i++) {
            writeBit(word >> i);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void write(int oneByte) throws IOException {
        writeBits(oneByte, 8);
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        super.close();

        while(currentBytePos > 0) {
            writeBit(0);
        }

        out.close();
    }
}
