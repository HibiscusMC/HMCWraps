package de.skyslycer.hmcwraps.util;

import org.bukkit.Material;

import java.util.Map;

public class MaterialUtil {

    private static final Map<String, Material[]> TYPE_ARMOR_MAPPING = Map.of(
            "LEATHER", new Material[]{Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS},
            "CHAINMAIL", new Material[]{Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS},
            "IRON", new Material[]{Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS},
            "GOLD", new Material[]{Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS},
            "DIAMOND", new Material[]{Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS},
            "NETHERITE", new Material[]{Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS}
    );

    /**
     * Get the alternate piece of an armor item
     *
     * @param type The new type
     * @param material The armor piece
     * @return The alternative
     */
    public static Material getAlternative(String type, Material material) {
        if (type != null && TYPE_ARMOR_MAPPING.containsKey(type)) {
            Material[] materials = TYPE_ARMOR_MAPPING.get(type);
            var split = material.toString().split("_");
            if (split.length != 2) {
                return material;
            }
            for (Material m : materials) {
                if (m.toString().contains(split[1])) {
                    return m;
                }
            }
        }
        return material;
    }

}
