package de.skyslycer.hmcwraps.util;

import java.util.stream.IntStream;

public class MathUtil {

    private static final double[] SIN_TABLE = IntStream.rangeClosed(0, 360).mapToDouble(i -> Math.sin(Math.toRadians(i))).toArray();

    /**
     * Get the corresponding sin value from an angle.
     *
     * @param angle The angle for the sin
     * @return The sin of the angle
     */
    public static double sin(int angle) {
        return SIN_TABLE[angle];
    }

}
