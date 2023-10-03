package de.skyslycer.hmcwraps.util;

import org.bukkit.Material;

public class MaterialUtil {

    /**
     * Get the leather alternative of an armor piece.
     *
     * @param material The armor piece
     * @return The leather alternative
     */
    public static Material getLeatherAlternative(Material material) {
        if (material.toString().contains("_HELMET")) {
            return Material.LEATHER_HELMET;
        } else if (material.toString().contains("_CHESTPLATE")) {
            return Material.LEATHER_CHESTPLATE;
        } else if (material.toString().contains("_LEGGINGS")) {
            return Material.LEATHER_LEGGINGS;
        } else if (material.toString().contains("_BOOTS")) {
            return Material.LEATHER_BOOTS;
        } else {
            return material;
        }
    }

}
