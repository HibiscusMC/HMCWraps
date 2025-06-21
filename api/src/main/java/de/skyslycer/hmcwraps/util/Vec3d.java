package de.skyslycer.hmcwraps.util;

import org.bukkit.Location;

public class Vec3d {

    private final double x;
    private final double y;
    private final double z;

    public Vec3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        return z;
    }

    public Vec3d subtract(double x, double y, double z) {
        return new Vec3d(this.x - x, this.y - y, this.z - z);
    }

    public Vec3d lowerY(boolean upsideDown) {
        return subtract(0, upsideDown ? 1 : 0.5, 0);
    }

    /**
     * Convert a Bukkit Location to a Vector3d.
     *
     * @param location The Bukkit location
     * @return The Vector3d
     */
    public static Vec3d fromLocation(Location location) {
        return new Vec3d(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Return a Vector3d with all 0s.
     *
     * @return The zero Vector3d
     */
    public static Vec3d zeroVector() {
        return new Vec3d(0d, 0d, 0d);
    }

}
