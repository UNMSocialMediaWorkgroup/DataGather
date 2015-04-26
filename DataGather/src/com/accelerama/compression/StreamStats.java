package com.accelerama.compression;

/**
 * Created by sixstring982 on 4/8/15.
 */
public class StreamStats {
    public final double ratio;
    public final long length;

    public StreamStats(double ratio, long length) {
        this.ratio = ratio;
        this.length = length;
    }
}
