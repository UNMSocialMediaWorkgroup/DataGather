package com.accelerama.gl.geometry;

/**
 * Created by Ross on 2/27/2015.
 */
public class Util {
    public static float radToDeg(float radians) {
        return radians * 57.2957795f;
    }

    public static float[] quaternionToEuclidean(float[] q) {
        return new float[] {
                (float)(Math.atan2(2 * (q[0] * q[1] + q[2] * q[3]),
                                  1 - 2 * (q[1] * q[1] + q[2] * q[2])) +
                                  Math.PI),
                (float)(Math.asin(2 * (q[0] * q[2] - q[3] * q[1])) +
                        Math.PI),
                (float)(Math.atan2(2 * (q[0] * q[3] + q[1] * q[2]),
                                  1 - 2 * (q[2] * q[2] + q[3] * q[3])) +
                                  Math.PI)
        };
    }
}
