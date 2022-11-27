package de.skyslycer.hmcwraps.util;

import com.github.retrooper.packetevents.util.Vector3d;
import org.bukkit.Location;

public class VectorUtils {

    /**
     * Convert a Bukkit Location to a PacketEvents Vector3d.
     *
     * @param location The Bukkit location
     * @return The Vector3d
     */
    public static Vector3d fromLocation(Location location) {
        return new Vector3d(location.getX(), location.getY(), location.getZ());
    }

    /**
     * Return a Vector3d with all 0s.
     *
     * @return The zero Vector3d
     */
    public static Vector3d zeroVector() {
        return new Vector3d(0d, 0d, 0d);
    }

}
