package de.skyslycer.hmcwraps.util;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class WrapNBTUtil {

    private static final String SAVE_KEY = "hmcwrapsoriginalnbt";

    /**
     * Applies the NBT to the item and saves the original NBT data in the item.
     *
     * @param stack The item to apply the NBT to
     * @param nbt   The NBT to apply
     * @return The item with the applied NBT data
     */
    public static ItemStack wrap(ItemStack stack, String nbt) {
        try {
            new NBTContainer(nbt);
        } catch (NbtApiException e) {
            Bukkit.getLogger().warning("A provided NBT data is invalid in a HMCWraps wrap!");
        }
        var itemNbt = new NBTItem(stack);
        var newNbt = NBT.parseNBT(nbt);
        var difference = itemNbt.getOrCreateCompound(SAVE_KEY);
        difference.clearNBT();
        apply(itemNbt, newNbt, difference);
        return itemNbt.getItem();
    }

    /**
     * Restore the original NBT data.
     *
     * @param stack The item to restore the NBT from
     * @return The item with the original NBT data
     */
    public static ItemStack unwrap(ItemStack stack) {
        var itemNbt = new NBTItem(stack);
        if (!itemNbt.hasTag(SAVE_KEY)) {
            return stack;
        }
        var originalNbt = itemNbt.getCompound(SAVE_KEY);
        rollback(originalNbt, itemNbt);
        itemNbt.removeKey(SAVE_KEY);
        return itemNbt.getItem();
    }

    private static void apply(NBTCompound original, ReadableNBT config, NBTCompound difference) {
        config.getKeys().forEach(key -> {
            if (key.equals(SAVE_KEY)) {
                return;
            }
            if ((!original.hasTag(key) || original.getType(key) == NBTType.NBTTagCompound) && config.getType(key) == NBTType.NBTTagCompound) {
                var originalCompound = original.getOrCreateCompound(key);
                var configCompound = config.getCompound(key);
                var differenceCompound = difference.addCompound(key);
                apply(originalCompound, configCompound, differenceCompound);
            } else {
                if (original.hasTag(key) && original.getType(key) == config.getType(key)) {
                    set(original, key, difference);
                } else {
                    difference.setByte(key + ".hmcwraps", (byte) 12);
                }
                set(config, key, original);
            }
        });
    }

    private static void rollback(ReadableNBT source, NBTCompound target) {
        source.getKeys().forEach(key -> {
            if (source.getType(key) == NBTType.NBTTagCompound) {
                var sourceCompound = source.getCompound(key);
                var targetCompound = target.getOrCreateCompound(key);
                rollback(sourceCompound, targetCompound);
                if (targetCompound.getKeys().isEmpty()) {
                    target.removeKey(key);
                }
            } else {
                if (key.endsWith(".hmcwraps") && source.getType(key) == NBTType.NBTTagByte && source.getByte(key) == 12 && target.hasTag(key.replace(".hmcwraps", ""))) {
                    target.removeKey(key.replace(".hmcwraps", ""));
                } else {
                    set(source, key, target);
                }
            }
        });
    }

    private static void set(ReadableNBT source, String key, NBTCompound target) {
        var type = source.getType(key);
        switch (type) {
            case NBTTagByte -> target.setByte(key, source.getByte(key));
            case NBTTagShort -> target.setShort(key, source.getShort(key));
            case NBTTagInt -> target.setInteger(key, source.getInteger(key));
            case NBTTagLong -> target.setLong(key, source.getLong(key));
            case NBTTagFloat -> target.setFloat(key, source.getFloat(key));
            case NBTTagDouble -> target.setDouble(key, source.getDouble(key));
            case NBTTagByteArray -> target.setByteArray(key, source.getByteArray(key));
            case NBTTagIntArray -> target.setIntArray(key, source.getIntArray(key));
            case NBTTagString -> target.setString(key, source.getString(key));
            case NBTTagList -> setList(source, key, target);
        }
    }

    private static void setList(ReadableNBT source, String key, NBTCompound target) {
        var type = source.getListType(key);
        switch (type) {
            case NBTTagInt -> target.getIntegerList(key).addAll(source.getIntegerList(key).toListCopy());
            case NBTTagFloat -> target.getFloatList(key).addAll(source.getFloatList(key).toListCopy());
            case NBTTagDouble -> target.getDoubleList(key).addAll(source.getDoubleList(key).toListCopy());
            case NBTTagIntArray -> target.getIntArrayList(key).addAll(source.getIntArrayList(key).toListCopy());
            case NBTTagString -> target.getStringList(key).addAll(source.getStringList(key).toListCopy());
            case NBTTagCompound -> target.getCompoundList(key).addAll(source.getCompoundList(key).toListCopy());
            case NBTTagLong -> target.getLongList(key).addAll(source.getLongList(key).toListCopy());
        }
    }

}
