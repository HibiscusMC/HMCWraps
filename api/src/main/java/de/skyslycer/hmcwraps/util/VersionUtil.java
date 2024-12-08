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
     * Get the patch Minecraft version.
     * 1.20.4 -> 4
     *
     * @return The minor Minecraft version
     */
    public static int getPatchMinecraftVersion() {
        var split = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
        return Integer.parseInt(split[2]);
    }

    /**
     * Check if trims are supported.
     *
     * @return If trims are supported
     */
    public static boolean trimsSupported() {
        return getMinorMinecraftVersion() >= 20;
    }

    /**
     * Check if the equippable component is supported.
     *
     * @return If the equippable component is supported
     */
    public static boolean equippableSupported() {
        if (getMinorMinecraftVersion() > 21) {
            return true;
        }
        return getMinorMinecraftVersion() == 21 && getPatchMinecraftVersion() >= 3;
    }

}
