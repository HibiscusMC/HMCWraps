package de.skyslycer.hmcwraps.util;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.inventory.ItemStack;

public class WrapNBTUtil {

    private static final String SAVE_KEY = "hmcwrapsoriginalnbt";

    /**
     * Applies the NBT to the item and saves the original NBT data in the item.
     *
     * @param stack The item to apply the NBT to
     * @param nbt The NBT to apply
     */
    public static void wrap(ItemStack stack, String nbt) {
        var itemNbt = new NBTItem(stack);
        var newNbt = NBT.parseNBT(nbt);
        var difference = itemNbt.getOrCreateCompound(SAVE_KEY);
        difference.clearNBT();
        apply(itemNbt, newNbt, difference);
    }

    /**
     * Restore the original NBT data.
     *
     * @param stack The item to restore the NBT from
     */
    public static void unwrap(ItemStack stack) {
        var itemNbt = new NBTItem(stack);
        if (!itemNbt.hasTag(SAVE_KEY)) {
            return;
        }
        var originalNbt = itemNbt.getCompound(SAVE_KEY);
        rollback(originalNbt, itemNbt);
        originalNbt.clearNBT();
    }

    private static void apply(NBTCompound original, ReadableNBT config, NBTCompound difference) {
        config.getKeys().forEach(key -> {
            if (key.equals(SAVE_KEY)) {
                return;
            }
            if (original.getType(key) == NBTType.NBTTagCompound && config.getType(key) == NBTType.NBTTagCompound) {
                var originalCompound = original.getCompound(key);
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
        }
    }

}
