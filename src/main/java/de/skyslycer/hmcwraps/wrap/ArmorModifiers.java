package de.skyslycer.hmcwraps.wrap;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public enum ArmorModifiers {

    LEATHER(0, 0, new ArmorValues(1, 2, 3, 1), new ArmorValues(15, 40, 30, 15)),
    CHAINMAIL(0, 0, new ArmorValues(2, 5, 4, 1), new ArmorValues(45, 120, 90, 45)),
    IRON(0, 0, new ArmorValues(2, 5, 6, 2), new ArmorValues(45, 120, 90, 45)),
    GOLD(0, 0, new ArmorValues(2, 5, 3, 1), new ArmorValues(21, 56, 42, 21)),
    DIAMOND(2, 0, new ArmorValues(3, 8, 6, 3), new ArmorValues(99, 264, 198, 99)),
    TURTLE(0, 0, new ArmorValues(2, 6, 5, 2), new ArmorValues(75, 200, 150, 75)),
    NETHERITE(3, 1, new ArmorValues(3, 8, 6, 3), new ArmorValues(111, 296, 222, 111));

    private final int toughness;
    private final int knockback;
    private final ArmorValues defense;
    private final ArmorValues durability;

    ArmorModifiers(int toughness, int knockback, ArmorValues defense, ArmorValues durability) {
        this.toughness = toughness;
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
        if (material.contains("LEATHER")) return LEATHER;
        return null;
    }

    public static void applyAttributes(ItemStack item, EquipmentSlot slot, int toughness, int knockback, int defense) {
        var meta = item.getItemMeta();
        if (meta.getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS) == null) {
           addModifier(meta, slot, Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
        } else {
            meta.getAttributeModifiers(Attribute.GENERIC_ARMOR_TOUGHNESS).stream().findFirst().ifPresent(modifier -> {
                meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, modifier);
                addModifier(meta, slot, Attribute.GENERIC_ARMOR_TOUGHNESS, toughness);
            });
        }
        if (knockback != 0) {
            if (meta.getAttributeModifiers(Attribute.GENERIC_KNOCKBACK_RESISTANCE) == null) {
                addModifier(meta, slot, Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockback / 10d);
            } else {
                meta.getAttributeModifiers(Attribute.GENERIC_KNOCKBACK_RESISTANCE).stream().findFirst().ifPresent(modifier -> {
                    meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, modifier);
                    addModifier(meta, slot, Attribute.GENERIC_KNOCKBACK_RESISTANCE, knockback / 10d); // divided by 10 because Minecraft decided so
                });
            }
        }
        if (meta.getAttributeModifiers(Attribute.GENERIC_ARMOR) == null) {
            addModifier(meta, slot, Attribute.GENERIC_ARMOR, defense);
        } else {
            meta.getAttributeModifiers(Attribute.GENERIC_ARMOR).stream().findFirst().ifPresent(modifier -> {
                meta.removeAttributeModifier(Attribute.GENERIC_ARMOR, modifier);
                addModifier(meta, slot, Attribute.GENERIC_ARMOR, defense);
            });
        }
        item.setItemMeta(meta);
    }

    public static ItemStack removeAttributes(ItemStack item) {
        var meta = item.getItemMeta();
        meta.removeAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS);
        meta.removeAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        meta.removeAttributeModifier(Attribute.GENERIC_ARMOR);
        item.setItemMeta(meta);
        return item;
    }

    private static void addModifier(ItemMeta meta, EquipmentSlot slot, Attribute attribute, double amount) {
        meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.getKey().getKey(), amount, AttributeModifier.Operation.ADD_NUMBER, slot));
    }

    public int getToughness() {
        return toughness;
    }

    public int getKnockback() {
        return knockback;
    }

    public ArmorValues getDefense() {
        return defense;
    }

    public ArmorValues getDurability() {
        return durability;
    }

}
