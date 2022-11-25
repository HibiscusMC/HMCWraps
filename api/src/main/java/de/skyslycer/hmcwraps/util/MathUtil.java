package de.skyslycer.hmcwraps.util;

import java.util.stream.IntStream;

public class MathUtil {

    private static final double[] SIN_TABLE = IntStream.rangeClosed(0, 360).mapToDouble(i -> Math.sin(Math.toRadians(i))).toArray();

    public static double sin(int angle) {
        return SIN_TABLE[angle];
    }

}
