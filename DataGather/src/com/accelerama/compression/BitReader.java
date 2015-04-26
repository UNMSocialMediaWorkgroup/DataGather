package com.accelerama.compression;

import java.io.IOException;
import java.io.InputStream;

/**
 * An IO Construct used to read groups of bits from an
 * {@link java.io.InputStream}.
 * <p>
 *     <b><i>Note:</i></b>
 *     This stream is far slower than other
 *     {@link java.io.InputStream}s, and really should only be used when needing
 *     IO on the bit (not byte) level.
 * </p>
 *
 * @author Six
 * @since March 2, 2015
 * @see com.lunagameserve.compression.BitWriter
 */
public class BitReader extends InputStream {

    /**
     * The current byte which has technically been read from the underlying
     * {@link java.io.InputStream}, though has not yet been read by the owner
     * of this {@link com.lunagameserve.compression.BitReader}.
     */
    private int currentByte = 0;

    /**
     * The bit number, in bits from the LSB of {@link #currentByte}, which
     * will be read from next upon {@link #readBit()}.
     */
    private int currentBytePos = 0;

    /**
     * The underlying {@link java.io.InputStream} which all
     */
    private final InputStream in;

    /**
     * Constructs a new {@link com.lunagameserve.compression.BitReader} whose
     * source bits will come from a specified {@link java.io.InputStream}.
     *
     * @param in The source of the bits which will be read by this
     *           {@link com.lunagameserve.compression.BitReader}.
     */
    public BitReader(InputStream in) {
        this.in = in;
    }

    /**
     * Reads a single bit from this {@link BitReader}. In the result of a legal
     * read, only the LSB will contain useful information, namely a {@code 1}
     * or a {@code 0}.
     *
     * @return {@code 1} if this stream contains a {@code 1} bit next, a
     *         {@code 0} if the next bit is a {@code 0}, or {@code -1} if there
     *         are no more bits to be read.
     *
     * @throws IOException if the underlying {@link java.io.InputStream} cannot
     *                     be read from for any reason.
     */
    public int readBit() throws IOException {
        if (currentBytePos == 0) {
            currentByte = in.read();
            if (currentByte == -1) {
                return -1;
            }
        }

        int readVal = currentByte & (1 << currentBytePos);
        if (readVal > 0) {
            readVal = 1;
        }
        currentBytePos++;
        currentBytePos %= 8;

        return readVal;
    }

    public boolean readBool() throws IOException {
        return readBit() > 0;
    }

    /**
     * Reads up to 32 bits from the underlying {@link java.io.InputStream}. If
     * there are less than 32 bits to be read, pads the rest of the return value
     * with zero bits.
     *
     * @param bits The number of bits to read from this
     *             {@link com.lunagameserve.compression.BitReader}.
     *
     * @return an {@code int} with {@code bits} LSB bits containing the bits
     *         read from the internal {@link java.io.InputStream}.
     *
     * @throws IOException If the internal {@link java.io.InputStream} cannot
     *                     be read from for any reason.
     *
     * @throws IllegalArgumentException if {@code bits} is not in the range
     *                                  {@code [1,32]}.
     */
    public int readBits(int bits) throws IOException {
        if (bits < 1 || bits > 32) {
            throw new IllegalArgumentException("Maximum bits read at one time" +
                                               " is 32 bits. Split this call" +
                                               " up into smaller chunks.");
        }
        int readByte = 0;
        for (int i = 0; i < bits; i++) {
            int bit = readBit();

            if (bit == -1) {
                if (currentBytePos == 0) {
                    return -1;
                } else {
                    return readByte;
                }
            }

            readByte |= (bit << i);
        }
        return readByte;
    }

    /** {@inheritDoc} */
    @Override
    public int read() throws IOException {
        return readBits(8);
    }
}
