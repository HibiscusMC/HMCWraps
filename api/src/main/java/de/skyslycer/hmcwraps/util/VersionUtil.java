package de.skyslycer.hmcwraps.util;

import org.bukkit.Bukkit;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public class VersionUtil {

    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

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
        return Integer.parseInt(split.length == 3 ? split[2] : "0");
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

    /**
     * Check if the item model component is supported. (1.21.4)
     *
     * @return If the item model component is supported
     */
    public static boolean itemModelSupported() {
        if (getMinorMinecraftVersion() > 21) {
            return true;
        }
        return getMinorMinecraftVersion() == 21 && getPatchMinecraftVersion() >= 4;
    }

    /**
     * Check if data components are supported.
     *
     * @return If the components are supported
     */
    public static boolean hasDataComponents() {
        if (getMinorMinecraftVersion() > 20) {
            return true;
        }
        return getMinorMinecraftVersion() == 20 && getPatchMinecraftVersion() >= 5;
    }

    /**
     * Check if the server uses interfaces instead of abstract classes.
     * This change was introduced in 1.21 and broke some methods, resulting in the need for reflection.
     *
     * @return If the server uses interfaces instead of abstract classes
     */
    public static boolean hasInterfaceInsteadOfAbstract() {
        return getMinorMinecraftVersion() >= 21;
    }

    /**
     * Get the open inventory type of a player.
     * This method uses reflection to get the method, unreflect it, and invoke using a method handle.
     *
     * @param player The player
     * @return The open inventory type
     */
    public static InventoryType getOpenInventoryType(Player player) {
        if (hasInterfaceInsteadOfAbstract()) {
            return player.getOpenInventory().getType();
        }
        try {
            Method getOpenInventoryMethod = Player.class.getMethod("getOpenInventory");
            MethodHandle getOpenInventoryHandle = LOOKUP.unreflect(getOpenInventoryMethod);
            Object openInventory = getOpenInventoryHandle.invoke(player);

            if (openInventory != null) {
                Method getTypeMethod = openInventory.getClass().getMethod("getType");
                getTypeMethod.setAccessible(true);
                MethodHandle getTypeHandle = LOOKUP.unreflect(getTypeMethod);
                Object type = getTypeHandle.invoke(openInventory);

                if (type instanceof InventoryType) {
                    return (InventoryType) type;
                } else {
                    throw new IllegalStateException("The returned type is not an InventoryType");
                }
            } else {
                return null;
            }
        } catch (Throwable t) {
            throw new RuntimeException("An error occurred while getting the open inventory type", t);
        }
    }

    /**
     * Get the item from a slot in an inventory click event.
     * This method uses reflection to get the method, unreflect it, and invoke using a method handle.
     *
     * @param event The inventory click event
     * @param slot The slot
     * @return The item in the slot
     */
    public static ItemStack getItemFromSlot(InventoryClickEvent event, int slot) {
        if (hasInterfaceInsteadOfAbstract()) {
            return event.getView().getItem(slot);
        }
        try {
            Method getViewMethod = InventoryClickEvent.class.getMethod("getView");
            MethodHandle getViewHandle = LOOKUP.unreflect(getViewMethod);
            Object view = getViewHandle.invoke(event);

            Method getItemMethod = view.getClass().getMethod("getItem", int.class);
            MethodHandle getItemHandle = LOOKUP.unreflect(getItemMethod);
            Object item = getItemHandle.invoke(view, slot);

            if (item instanceof ItemStack) {
                return (ItemStack) item;
            } else {
                throw new IllegalStateException("The returned object is not an ItemStack");
            }
        } catch (Throwable t) {
            throw new RuntimeException("An error occurred while getting the item from the slot", t);
        }
    }

    /**
     * Set the item in a slot in an inventory click event.
     * This method uses reflection to get the method, unreflect it, and invoke using a method handle.
     *
     * @param event The inventory click event
     * @param slot The slot
     * @param updatedItem The updated item
     */
    public static void setItemInSlot(InventoryClickEvent event, int slot, ItemStack updatedItem) {
        if (hasInterfaceInsteadOfAbstract()) {
            event.getView().setItem(slot, updatedItem);
            return;
        }
        try {
            Method getViewMethod = InventoryClickEvent.class.getMethod("getView");
            MethodHandle getViewHandle = LOOKUP.unreflect(getViewMethod);
            Object view = getViewHandle.invoke(event);

            Method setItemMethod = view.getClass().getMethod("setItem", int.class, ItemStack.class);
            MethodHandle setItemHandle = LOOKUP.unreflect(setItemMethod);
            setItemHandle.invoke(view, slot, updatedItem);
        } catch (Throwable t) {
            throw new RuntimeException("An error occurred while setting the item in the slot", t);
        }
    }

    /**
     * Get the top inventory of a player.
     * This method uses reflection to get the method, unreflect it, and invoke using a method handle.
     *
     * @param player The player
     * @return The top inventory
     */
    public static Inventory getTopInventory(Player player) {
        if (hasInterfaceInsteadOfAbstract()) {
            return player.getOpenInventory().getTopInventory();
        }
        try {
            Method getOpenInventoryMethod = Player.class.getMethod("getOpenInventory");
            MethodHandle getOpenInventoryHandle = LOOKUP.unreflect(getOpenInventoryMethod);
            Object openInventory = getOpenInventoryHandle.invoke(player);

            if (openInventory != null) {
                Method getTopInventoryMethod = openInventory.getClass().getMethod("getTopInventory");
                getTopInventoryMethod.setAccessible(true);
                MethodHandle getTopInventoryHandle = LOOKUP.unreflect(getTopInventoryMethod);
                Object topInventory = getTopInventoryHandle.invoke(openInventory);

                if (topInventory instanceof Inventory) {
                    return (Inventory) topInventory;
                } else {
                    throw new IllegalStateException("The returned object is not an Inventory");
                }
            } else {
                return null;
            }
        } catch (Throwable t) {
            throw new RuntimeException("An error occurred while getting the top inventory", t);
        }
    }

    /**
     * Get the bottom inventory of a player.
     * This method uses reflection to get the method, unreflect it, and invoke using a method handle.
     *
     * @param player The player
     * @return The bottom inventory
     */
    public static Inventory getBottomInventory(Player player) {
        if (hasInterfaceInsteadOfAbstract()) {
            return player.getOpenInventory().getBottomInventory();
        }
        try {
            Method getOpenInventoryMethod = Player.class.getMethod("getOpenInventory");
            MethodHandle getOpenInventoryHandle = LOOKUP.unreflect(getOpenInventoryMethod);
            Object openInventory = getOpenInventoryHandle.invoke(player);

            if (openInventory != null) {
                Method getBottomInventoryMethod = openInventory.getClass().getMethod("getBottomInventory");
                getBottomInventoryMethod.setAccessible(true);
                MethodHandle getBottomInventoryHandle = LOOKUP.unreflect(getBottomInventoryMethod);
                Object bottomInventory = getBottomInventoryHandle.invoke(openInventory);

                if (bottomInventory instanceof Inventory) {
                    return (Inventory) bottomInventory;
                } else {
                    throw new IllegalStateException("The returned object is not an Inventory");
                }
            } else {
                return null;
            }
        } catch (Throwable t) {
            throw new RuntimeException("An error occurred while getting the bottom inventory", t);
        }
    }

    /**
     * Useful attribute methods using reflections to circumvent API changes.
     */
    public enum Attribute {
        ARMOR_TOUGHNESS("ARMOR_TOUGHNESS", "GENERIC_ARMOR_TOUGHNESS"),
        KNOCKBACK_RESISTANCE("KNOCKBACK_RESISTANCE", "GENERIC_KNOCKBACK_RESISTANCE"),
        ARMOR("ARMOR", "GENERIC_ARMOR");

        private final String modernAttribute;
        private final String legacyAttribute;

        Attribute(String modernAttribute, String legacyAttribute) {
            this.modernAttribute = modernAttribute;
            this.legacyAttribute = legacyAttribute;
        }

        /**
         * Get the server attribute object from this version independent enum.
         *
         * @return The attribute object
         */
        public Object getAttribute() {
            if (VersionUtil.equippableSupported()) {
                try {
                    return org.bukkit.attribute.Attribute.valueOf(modernAttribute);
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

        /**
         * Remove an attribute modifier from an item meta.
         *
         * @param meta The item meta
         * @param attribute The attribute to remove
         */
        public static void removeAttributeModifier(ItemMeta meta, Attribute attribute) {
            var attributeObject = attribute.getAttribute();
            if (VersionUtil.equippableSupported()) {
                try {
                    meta.removeAttributeModifier((org.bukkit.attribute.Attribute) attributeObject);
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

        /**
         * Add an attribute modifier to an item meta.
         *
         * @param meta The item meta
         * @param slot The equipment slot
         * @param attribute The attribute to add
         * @param amount The amount to add
         */
        public static void addAttributeModifier(ItemMeta meta, EquipmentSlot slot, Attribute attribute, double amount) {
            var attributeObject = attribute.getAttribute();
            if (VersionUtil.equippableSupported()) {
                var modernAttribute = (org.bukkit.attribute.Attribute) attributeObject;
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

        /**
         * Create an attribute modifier with support for legacy versions.
         *
         * @param attributeObject The attribute object
         * @param amount The amount to add
         * @param slot The equipment slot
         * @return The created attribute modifier
         */
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
