package de.skyslycer.hmcwraps.util;

import org.bukkit.Bukkit;

public class VersionUtil {

    /**
     * Get the minor Minecraft version.
     * 1.20.4 -> 20
     *
     * @return The minor Minecraft version
     */
    public static int getMinorMinecraftVersion() {
        var split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        return Integer.parseInt(split[1]);
    }

    /**
     * Check if trims are supported.
     *
     * @return If trims are supported
     */
    public static boolean trimsSupported() {
        return getMinorMinecraftVersion() >= 20;
    }

}
