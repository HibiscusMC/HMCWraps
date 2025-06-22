package de.skyslycer.hmcwraps.util;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
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
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
     * Check if the current server version is supported.
     *
     * @return If the current server version is supported
     */
    public static boolean isSupported() {
        return getMinorMinecraftVersion() >= 20 && getPatchMinecraftVersion() >= 4;
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
                return null;
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
                    return null;
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
                    return null;
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
                    throw new IllegalArgumentException("Failed to get modern attribute: " + modernAttribute, exception);
                }
            } else {
                try {
                    Class<?> attributeClass = Class.forName("org.bukkit.attribute.Attribute");
                    Method valueOfMethod = attributeClass.getMethod("valueOf", String.class);
                    return valueOfMethod.invoke(null, legacyAttribute);
                } catch (Exception exception) {
                    throw new IllegalArgumentException("Failed to get legacy attribute: " + legacyAttribute, exception);
                }
            }
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
                    throw new IllegalArgumentException("Failed to remove modern attribute: " + attribute.name(), exception);
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
                    throw new IllegalArgumentException("Failed to remove legacy attribute: " + attribute.name(), exception);
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
                    meta.addAttributeModifier(modernAttribute, new AttributeModifier(
                            NamespacedKey.minecraft(RandomUtil.generateRandomId()), amount,
                            AttributeModifier.Operation.ADD_NUMBER, slot.getGroup()));
                } catch (IllegalArgumentException exception) {
                    throw new IllegalArgumentException("Failed to add modern attribute: " + attribute.name(), exception);
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
                    throw new IllegalArgumentException("Failed to add legacy attribute: " + attribute.name(), exception);
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
                throw new IllegalArgumentException("Failed to create attribute modifier for " + attributeObject, exception);
            }
        }
    }

    /**
     * Send a fake item to a player in a specific slot.
     *
     * @param player The player to send the item to
     * @param item The item to send
     * @param slot The slot to send the item to
     */
    public static void sendFakeItem(Player player, ItemStack item, int slot) {
        var craftItemStackClassName = VersionUtil.hasDataComponents() ? "org.bukkit.craftbukkit.inventory.CraftItemStack"
                : "org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack";
        var packetClassName = VersionUtil.hasDataComponents() ? "net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket"
                : "net.minecraft.network.protocol.game.PacketPlayOutSetSlot";
        try {
            Class<?> craftItemStackClass = Class.forName(craftItemStackClassName);
            Method asNMSCopy = craftItemStackClass.getMethod("asNMSCopy", ItemStack.class);
            Object nmsItem = asNMSCopy.invoke(null, item);

            Class<?> packetClass = Class.forName(packetClassName);
            Constructor<?> packetConstructor = packetClass.getConstructor(int.class, int.class, int.class, nmsItem.getClass());
            Object packet = packetConstructor.newInstance(0, -1, slot, nmsItem);

            sendPacket(player, packet);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send fake item packet", exception);
        }
    }

    /**
     * Send a spawn packet for an armor stand to a player for previewing wraps.
     *
     * @param player The player to send the packet to
     * @param entityId The entity ID of the armor stand
     * @param upsideDown If the displayed item should be upside down
     */
    public static void sendSpawnPacket(Player player, int entityId, boolean upsideDown) {
        var packetClassName = VersionUtil.hasDataComponents() ? "net.minecraft.network.protocol.game.ClientboundAddEntityPacket"
                : "net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity";
        var entityTypeClassName = VersionUtil.hasDataComponents() ? "net.minecraft.world.entity.EntityType"
                : "net.minecraft.world.entity.EntityTypes";
        var vec3ClassName = VersionUtil.hasDataComponents() ? "net.minecraft.world.phys.Vec3"
                : "net.minecraft.world.phys.Vec3D";
        var vec3ZeroName = VersionUtil.hasDataComponents() ? "ZERO" : "b";
        var entityTypeName = VersionUtil.hasDataComponents() ? "ARMOR_STAND" : "d";
        try {
            var position = Vec3d.fromLocation(PlayerUtil.getLookBlock(player)).lowerY(upsideDown);
            var packetClass = Class.forName(packetClassName);
            var entityTypeClass = Class.forName(entityTypeClassName);
            var vec3Class = Class.forName(vec3ClassName);
            var armorStandType = entityTypeClass.getField(entityTypeName).get(null);
            var zeroVec3 = vec3Class.getField(vec3ZeroName).get(null);
            var constructor = packetClass.getConstructor(
                    int.class,            // entity ID
                    UUID.class,           // UUID
                    double.class,         // x
                    double.class,         // y
                    double.class,         // z
                    float.class,          // yaw
                    float.class,          // pitch
                    entityTypeClass,      // EntityType
                    int.class,            // data
                    vec3Class,            // velocity
                    double.class          // headYaw
            );
            var packet = constructor.newInstance(entityId, UUID.randomUUID(), position.x(), position.y(), position.z(),
                    0f, 0f, armorStandType, 0, zeroVec3, 0d);
            sendPacket(player, packet);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send armor stand spawn packet", exception);
        }
    }

    /**
     * Send a metadata packet to a player for the preview.
     *
     * @param player The player to send the packet to
     * @param entityId The entity ID of the armor stand
     * @param upsideDown If the displayed item should be upside down
     */
    public static void sendMetadataPacket(Player player, int entityId, boolean upsideDown) {
        var packetClassName = VersionUtil.hasDataComponents() ? "net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket"
                : "net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata";
        var dataValueClassName = VersionUtil.hasDataComponents() ? "net.minecraft.network.syncher.SynchedEntityData$DataValue"
                : "net.minecraft.network.syncher.DataWatcher$b";
        var entityDataSerializersClassName = VersionUtil.hasDataComponents() ? "net.minecraft.network.syncher.EntityDataSerializers"
                : "net.minecraft.network.syncher.DataWatcherRegistry";
        var rotationsClassName = VersionUtil.hasDataComponents() ? "net.minecraft.core.Rotations" : "net.minecraft.core.Vector3f";
        var byteFieldName = VersionUtil.hasDataComponents() ? "BYTE" : "a";
        var booleanFieldName = VersionUtil.hasDataComponents() ? "BOOLEAN" : "k";
        var rotationsFieldName = VersionUtil.hasDataComponents() ? "ROTATIONS" : "m";
        var entityDataSerializerClassName = VersionUtil.hasDataComponents() ? "net.minecraft.network.syncher.EntityDataSerializer"
                : "net.minecraft.network.syncher.DataWatcherSerializer";
        try {
            Class<?> packetClass = Class.forName(packetClassName);
            Class<?> dataValueClass = Class.forName(dataValueClassName);
            Class<?> entityDataSerializersClass = Class.forName(entityDataSerializersClassName);
            Class<?> rotationsClass = Class.forName(rotationsClassName);

            Object byteSerializer = entityDataSerializersClass.getField(byteFieldName).get(null);
            Object booleanSerializer = entityDataSerializersClass.getField(booleanFieldName).get(null);
            Object rotationsSerializer = entityDataSerializersClass.getField(rotationsFieldName).get(null);

            Constructor<?> rotationsConstructor = rotationsClass.getConstructor(float.class, float.class, float.class);
            Object rotations = rotationsConstructor.newInstance(upsideDown ? 0f : 180f, 0f, 0f);

            Constructor<?> dataValueConstructor = dataValueClass.getConstructor(int.class, Class.forName(entityDataSerializerClassName), Object.class);
            Object value0 = dataValueConstructor.newInstance(0, byteSerializer, (byte) 0x20);
            Object value16 = dataValueConstructor.newInstance(16, rotationsSerializer, rotations);
            Object value5 = dataValueConstructor.newInstance(5, booleanSerializer, true);
            List<Object> metadataList = List.of(value0, value16, value5);

            Constructor<?> packetConstructor = packetClass.getConstructor(int.class, List.class);
            Object packet = packetConstructor.newInstance(entityId, metadataList);

            sendPacket(player, packet);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send armor stand metadata packet", exception);
        }
    }

    /**
     * Send a teleport packet to a player for the preview.
     *
     * @param player The player to send the packet to
     * @param entityId The entity ID of the armor stand
     * @param upsideDown If the displayed item should be upside down
     */
    public static void sendTeleportPacket(Player player, int entityId, boolean upsideDown) {
        try {
            Vec3d position = Vec3d.fromLocation(PlayerUtil.getLookBlock(player)).lowerY(upsideDown);
            if (VersionUtil.equippableSupported()) {
                Class<?> teleportPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket");
                Class<?> positionClass = Class.forName("net.minecraft.world.entity.PositionMoveRotation");
                Class<?> vec3Class = Class.forName("net.minecraft.world.phys.Vec3");

                Object vec3Zero = vec3Class.getField("ZERO").get(null);
                Constructor<?> vec3Constructor = vec3Class.getConstructor(double.class, double.class, double.class);
                Object vec3Position = vec3Constructor.newInstance(position.x(), position.y(), position.z());

                Constructor<?> posRotConstructor = positionClass.getConstructor(vec3Class, vec3Class, float.class, float.class);
                Object posRot = posRotConstructor.newInstance(vec3Position, vec3Zero, 0f, 0f);

                Method teleportFactory = teleportPacketClass.getMethod("teleport", int.class, positionClass, Set.class, boolean.class);
                Object packet = teleportFactory.invoke(null, entityId, posRot, Set.of(), false);

                sendPacket(player, packet);
            } else if (VersionUtil.hasDataComponents()) {
                Class<?> byteBufClass = Class.forName("net.minecraft.network.FriendlyByteBuf");
                Class<?> teleportPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket");

                Class<?> unpooledClass = Class.forName("io.netty.buffer.Unpooled");
                Class<?> byteBufInterface = Class.forName("io.netty.buffer.ByteBuf");
                Object byteBufInstance = unpooledClass.getMethod("buffer").invoke(null);

                Constructor<?> byteBufConstructor = byteBufClass.getConstructor(byteBufInterface);
                Object byteBuf = byteBufConstructor.newInstance(byteBufInstance);

                Method writeVarInt = byteBufClass.getMethod("writeVarInt", int.class);
                Method writeDouble = byteBufClass.getMethod("writeDouble", double.class);
                Method writeByte = byteBufClass.getMethod("writeByte", int.class);
                Method writeBoolean = byteBufClass.getMethod("writeBoolean", boolean.class);

                writeVarInt.invoke(byteBuf, entityId);
                writeDouble.invoke(byteBuf, position.x());
                writeDouble.invoke(byteBuf, position.y());
                writeDouble.invoke(byteBuf, position.z());
                writeByte.invoke(byteBuf, (byte) 0); // yaw
                writeByte.invoke(byteBuf, (byte) 0); // pitch
                writeBoolean.invoke(byteBuf, false); // onGround

                Constructor<?> teleportConstructor = teleportPacketClass.getDeclaredConstructor(byteBufClass);
                teleportConstructor.setAccessible(true);
                Object packet = teleportConstructor.newInstance(byteBuf);

                sendPacket(player, packet);
            } else {
                // Legacy version using PacketDataSerializer
                Class<?> packetClass = Class.forName("net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport");
                Class<?> serializerClass = Class.forName("net.minecraft.network.PacketDataSerializer");
                Class<?> unpooledClass = Class.forName("io.netty.buffer.Unpooled");
                Object byteBuf = unpooledClass.getMethod("buffer").invoke(null);
                Constructor<?> serializerCtor = serializerClass.getConstructor(Class.forName("io.netty.buffer.ByteBuf"));
                Object serializer = serializerCtor.newInstance(byteBuf);

                Method writeInt = serializerClass.getMethod("c", int.class);
                Method writeDouble = serializerClass.getMethod("a", double.class);
                Method writeByte = serializerClass.getMethod("k", int.class);
                Method writeBoolean = serializerClass.getMethod("a", boolean.class);

                writeInt.invoke(serializer, entityId);
                writeDouble.invoke(serializer, position.x());
                writeDouble.invoke(serializer, position.y());
                writeDouble.invoke(serializer, position.z());
                writeByte.invoke(serializer, 0); // yaw
                writeByte.invoke(serializer, 0); // pitch
                writeBoolean.invoke(serializer, false); // onGround

                Constructor<?> packetConstructor = packetClass.getDeclaredConstructor(serializerClass);
                packetConstructor.setAccessible(true);
                Object packet = packetConstructor.newInstance(serializer);

                sendPacket(player, packet);
            }
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send armor stand teleport packet", exception);
        }
    }

    /**
     * Send an equip packet to a player for the preview.
     *
     * @param player The player to send the packet to
     * @param entityId The entity ID of the armor stand
     * @param item The item to equip
     */
    public static void sendEquipPacket(Player player, int entityId, ItemStack item) {
        var craftItemStackClassName = VersionUtil.hasDataComponents() ? "org.bukkit.craftbukkit.inventory.CraftItemStack"
                : "org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack";
        var equipmentSlotClassName = VersionUtil.hasDataComponents() ? "net.minecraft.world.entity.EquipmentSlot"
                : "net.minecraft.world.entity.EnumItemSlot";
        var headSlotName = VersionUtil.hasDataComponents() ? "HEAD" : "f";
        var packetName = VersionUtil.hasDataComponents() ? "net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket"
                : "net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment";
        try {
            Object nmsItem = Class.forName(craftItemStackClassName)
                    .getMethod("asNMSCopy", ItemStack.class)
                    .invoke(null, item);

            Class<?> pairClass = Class.forName("com.mojang.datafixers.util.Pair");
            Class<?> equipmentSlotClass = Class.forName(equipmentSlotClassName);
            Class<?> clientboundEquipPacketClass = Class.forName(packetName);

            Object headSlot = equipmentSlotClass.getField(headSlotName).get(null);
            Method pairOfMethod = pairClass.getMethod("of", Object.class, Object.class);
            Object pair = pairOfMethod.invoke(null, headSlot, nmsItem);
            List<Object> pairList = List.of(pair);

            Constructor<?> packetConstructor = clientboundEquipPacketClass.getConstructor(int.class, List.class);
            Object packet = packetConstructor.newInstance(entityId, pairList);

            sendPacket(player, packet);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send armor stand equip packet", exception);
        }
    }

    /**
     * Send a destroy packet to a player for the preview.
     *
     * @param player The player to send the packet to
     * @param entityId The entity ID of the armor stand
     */
    public static void sendDestroyPacket(Player player, int entityId) {
        var removeEntitiesPacketClassName = VersionUtil.hasDataComponents() ? "net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket"
                : "net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy";
        try {
            Class<?> removeEntitiesPacketClass = Class.forName(removeEntitiesPacketClassName);
            Constructor<?> constructor = removeEntitiesPacketClass.getConstructor(int[].class);
            Object packet = constructor.newInstance((Object) new int[] { entityId });
            sendPacket(player, packet);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send destroy packet", exception);
        }
    }

    /**
     * Send a relative move and rotate packet to a player for the preview.
     *
     * @param player The player to send the packet to
     * @param entityId The entity ID of the armor stand
     * @param yDiff The difference in Y position
     * @param currentAngle The current angle of the armor stand
     */
    public static void sendRelativeMoveAndRotatePacket(Player player, int entityId, double yDiff, float currentAngle) {
        var posRotClassName = VersionUtil.hasDataComponents() ? "net.minecraft.network.protocol.game.ClientboundMoveEntityPacket$PosRot"
                : "net.minecraft.network.protocol.game.PacketPlayOutEntity$PacketPlayOutRelEntityMoveLook";
        try {
            Class<?> posRotClass = Class.forName(posRotClassName);
            Constructor<?> constructor = posRotClass.getConstructor(
                    int.class,    // entityId
                    short.class,  // deltaX
                    short.class,  // deltaY
                    short.class,  // deltaZ
                    byte.class,   // yaw
                    byte.class,   // pitch
                    boolean.class // onGround
            );
            Object packet = constructor.newInstance(entityId, (short) 0, (short) (yDiff * 4096.0), (short) 0,
                    (byte) (currentAngle * 0.7111111F), (byte) (90f * 0.7111111F), false);
            sendPacket(player, packet);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send relative move and rotate packet", exception);
        }
    }

    /**
     * Get the next entity ID for spawning entities.
     * This method uses reflection to access the nextEntityId method.
     *
     * @return The next entity ID
     */
    public static int getNextEntityId() {
        try {
            var entityClass = Class.forName("net.minecraft.world.entity.Entity");
            var nextEntityIdMethod = entityClass.getMethod("nextEntityId");
            var result = nextEntityIdMethod.invoke(null);
            return (int) result;
        } catch (Exception exception) {
            throw new RuntimeException("Failed to get next entity ID", exception);
        }
    }

    private static void sendPacket(Player player, Object packet) {
        var craftPlayerClassName = VersionUtil.hasDataComponents() ? "org.bukkit.craftbukkit.entity.CraftPlayer"
                : "org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer";
        var connectionFieldName = VersionUtil.hasDataComponents() ? "connection" : "c";
        var sendMethodName = VersionUtil.hasDataComponents() ? "send" : "b";
        try {
            var craftPlayerClass = Class.forName(craftPlayerClassName);
            var getHandle = craftPlayerClass.getMethod("getHandle");
            var handle = getHandle.invoke(player);

            var connectionField = handle.getClass().getField(connectionFieldName);
            var connection = connectionField.get(handle);

            var sendMethod = connection.getClass().getMethod(sendMethodName, Class.forName("net.minecraft.network.protocol.Packet"));
            sendMethod.invoke(connection, packet);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to send packet to player: " + player.getName(), exception);
        }
    }

}
