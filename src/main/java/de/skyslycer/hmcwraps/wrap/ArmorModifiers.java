package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.util.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

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
        VersionAttribute.removeAttributeModifier(meta, VersionAttribute.ARMOR_TOUGHNESS);
        VersionAttribute.addAttributeModifier(meta, slot, VersionAttribute.ARMOR_TOUGHNESS, toughness);
        VersionAttribute.removeAttributeModifier(meta, VersionAttribute.ARMOR);
        VersionAttribute.addAttributeModifier(meta, slot, VersionAttribute.ARMOR, defense);
        if (knockback != 0) {
            VersionAttribute.removeAttributeModifier(meta, VersionAttribute.KNOCKBACK_RESISTANCE);
            VersionAttribute.addAttributeModifier(meta, slot, VersionAttribute.KNOCKBACK_RESISTANCE, knockback / 10d); // divided by 10 because Minecraft decided so
        }
        item.setItemMeta(meta);
    }

    public static ItemStack removeAttributes(ItemStack item) {
        var meta = item.getItemMeta();
        VersionAttribute.removeAttributeModifier(meta, VersionAttribute.ARMOR_TOUGHNESS);
        VersionAttribute.removeAttributeModifier(meta, VersionAttribute.KNOCKBACK_RESISTANCE);
        VersionAttribute.removeAttributeModifier(meta, VersionAttribute.ARMOR);
        item.setItemMeta(meta);
        return item;
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

    private enum VersionAttribute {
        ARMOR_TOUGHNESS("ARMOR_TOUGHNESS", "GENERIC_ARMOR_TOUGHNESS"),
        KNOCKBACK_RESISTANCE("KNOCKBACK_RESISTANCE", "GENERIC_KNOCKBACK_RESISTANCE"),
        ARMOR("ARMOR", "GENERIC_ARMOR");

        private final String modernAttribute;
        private final String legacyAttribute;

        VersionAttribute(String modernAttribute, String legacyAttribute) {
            this.modernAttribute = modernAttribute;
            this.legacyAttribute = legacyAttribute;
        }

        public Object getAttribute() {
            if (VersionUtil.equippableSupported()) {
                try {
                    return Attribute.valueOf(modernAttribute);
                } catch (IllegalArgumentException exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to get modern attribute: " + modernAttribute, exception);
                }
            } else {
                try {
                    Class<?> attributeClass = Class.forName("org.bukkit.attribute.Attribute");
                    Method valueOfMethod = attributeClass.getMethod("valueOf", String.class);
                    return valueOfMethod.invoke(null, legacyAttribute);
                } catch (Exception exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to get legacy attribute: " + legacyAttribute, exception);
                }
            }
            return null;
        }

        public static void removeAttributeModifier(ItemMeta meta, VersionAttribute attribute) {
            var attributeObject = attribute.getAttribute();
            if (VersionUtil.equippableSupported()) {
                try {
                    meta.removeAttributeModifier((Attribute) attributeObject);
                } catch (IllegalArgumentException exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to remove modern attribute: " + attribute.name(), exception);
                }
            } else {
                try {
                    Class<?> attributeClass = Class.forName("org.bukkit.attribute.Attribute");
                    if (!attributeClass.isInstance(attributeObject)) {
                        throw new IllegalArgumentException("Provided object is not a valid Attribute instance");
                    }
                    Object castedAttribute = attributeClass.cast(attributeObject);
                    Method removeModifierMethod = ItemMeta.class.getMethod("removeAttributeModifier", attributeClass);
                    removeModifierMethod.invoke(meta, castedAttribute);
                } catch (Exception exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to remove legacy attribute: " + attribute.name(), exception);
                }
            }
        }

        public static void addAttributeModifier(ItemMeta meta, EquipmentSlot slot, VersionAttribute attribute, double amount) {
            var attributeObject = attribute.getAttribute();
            if (VersionUtil.equippableSupported()) {
                var modernAttribute = (Attribute) attributeObject;
                try {
                    meta.addAttributeModifier(modernAttribute, new AttributeModifier(modernAttribute.getKey(), amount, AttributeModifier.Operation.ADD_NUMBER, slot.getGroup()));
                } catch (IllegalArgumentException exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to add modern attribute: " + attribute.name(), exception);
                }
            } else {
                try {
                    Class<?> attributeClass = Class.forName("org.bukkit.attribute.Attribute");
                    if (!attributeClass.isInstance(attributeObject)) {
                        throw new IllegalArgumentException("Provided object is not a valid Attribute instance");
                    }
                    Object castedAttribute = attributeClass.cast(attributeObject);
                    var modifier = createAttributeModifier(castedAttribute, amount, slot);
                    var addModifierMethod = ItemMeta.class.getMethod("addAttributeModifier", attributeClass, AttributeModifier.class);
                    addModifierMethod.invoke(meta, castedAttribute, modifier);
                } catch (Exception exception) {
                    Bukkit.getLogger().log(Level.SEVERE, "Failed to add legacy attribute: " + attribute.name(), exception);
                }
            }
        }

        public static Object createAttributeModifier(Object attributeObject, double amount, EquipmentSlot slot) {
            try {
                Class<?> attributeModifierClass = Class.forName("org.bukkit.attribute.AttributeModifier");
                Constructor<?> constructor = attributeModifierClass.getConstructor(
                        UUID.class,
                        String.class,
                        double.class,
                        AttributeModifier.Operation.class,
                        EquipmentSlot.class
                );
                return constructor.newInstance(
                        UUID.randomUUID(),
                        attributeObject.toString(),
                        amount,
                        AttributeModifier.Operation.ADD_NUMBER,
                        slot
                );
            } catch (Exception exception) {
                Bukkit.getLogger().log(Level.SEVERE, "Failed to create legacy attribute modifier!", exception);
            }
            return null;
        }
    }

}
