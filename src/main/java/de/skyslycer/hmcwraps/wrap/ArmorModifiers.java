package de.skyslycer.hmcwraps.wrap;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum ArmorModifiers {

    CHAINMAIL(0, 0f, new ArmorValues(1, 2, 3, 1), new ArmorValues(15, 40, 30, 15)),
    IRON(0, 0f, new ArmorValues(2, 5, 6, 2), new ArmorValues(45, 120, 90, 45)),
    GOLD(0, 0f, new ArmorValues(2, 5, 3, 1), new ArmorValues(21, 56, 42, 21)),
    DIAMOND(2, 0f, new ArmorValues(3, 8, 6, 3), new ArmorValues(99, 264, 198, 99)),
    TURTLE(0, 0f, new ArmorValues(2, 6, 5, 2), new ArmorValues(75, 200, 150, 75)),
    NETHERITE(3, 0.1f, new ArmorValues(3, 8, 6, 3), new ArmorValues(111, 296, 222, 111));

    private int toughness;
    private float knockback;
    private ArmorValues defense;
    private ArmorValues durability;

    ArmorModifiers(int tougness, float knockback, ArmorValues defense, ArmorValues durability) {
        this.toughness = tougness;
        this.knockback = knockback;
        this.defense = defense;
        this.durability = durability;
    }

    public record ArmorValues(int helmet, int chestplate, int leggings, int boots) { }

    public static ArmorModifiers getFromMaterial(String material) {
        if (material.contains("CHAINMAIL")) return CHAINMAIL;
        if (material.contains("IRON")) return IRON;
        if (material.contains("GOLD")) return GOLD;
        if (material.contains("DIAMOND")) return DIAMOND;
        if (material.contains("TURTLE")) return TURTLE;
        if (material.contains("NETHERITE")) return NETHERITE;
        return null;
    }

    public static ItemStack applyAttributes(ItemStack item, int toughness, float knockback, int defense) {
        var meta = item.getItemMeta();
        if (meta.getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS) == null) {
           addModifier(meta, Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
        } else {
            meta.getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS).stream().findFirst().ifPresent(modifier -> {
                meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier);
                addModifier(meta, Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
            });
        }
        if (meta.getAttributeModifiers(Attribute.GENERIC_KNOCKBACK_RESISTANCE) == null) {
            addModifier(meta, Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockback);
        } else {
            meta.getAttributeModifiers(Attribute.GENERIC_KNOCKBACK_RESISTANCE).stream().findFirst().ifPresent(modifier -> {
                meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier);
                addModifier(meta, Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockback);
            });
        }
        if (meta.getAttributeModifiers(Attribute.GENERIC_ARMOR) == null) {
            addModifier(meta, Attribute.GENERIC_ARMOR, defense);
        } else {
            meta.getAttributeModifiers(Attribute.GENERIC_ARMOR).stream().findFirst().ifPresent(modifier -> {
                meta.removeAttributeModifier(Attribute.GENERIC_ARMOR, modifier);
                addModifier(meta, Attribute.GENERIC_ARMOR, defense);
            });
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack removeAttributes(ItemStack item) {
        var meta = item.getItemMeta();
        meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
        meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
        item.setItemMeta(meta);
        return item;
    }

    private static void addModifier(ItemMeta meta, Attribute attribute, double amount) {
        meta.addAttributeModifier(attribute, new AttributeModifier(attribute.getKey().getKey(), amount, AttributeModifier.Operation.ADD_NUMBER));
    }

    public int getToughness() {
        return toughness;
    }

    public float getKnockback() {
        return knockback;
    }

    public ArmorValues getDefense() {
        return defense;
    }

    public ArmorValues getDurability() {
        return durability;
    }

}
