package de.skyslycer.hmcwraps.util;

import com.github.retrooper.packetevents.util.Vector3d;
import org.bukkit.Location;

public class VectorUtils {

    public static Vector3d fromLocation(Location location) {
        return new Vector3d(location.getX(), location.getY(), location.getZ());
    }

    public static Vector3d zeroVector() {
        return new Vector3d(0d, 0d, 0d);
    }

}
