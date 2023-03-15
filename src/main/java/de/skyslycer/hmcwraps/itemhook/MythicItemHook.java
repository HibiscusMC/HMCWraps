package de.skyslycer.hmcwraps.itemhook;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

public class MythicItemHook implements ItemHook {

    @Override
    public String getPrefix() {
        return "mythic:";
    }

    @Override
    public ItemStack get(String id) {
        if (MythicBukkit.inst().getItemManager().getItem(id).isEmpty()) {
            return null;
        }
        return MythicBukkit.inst().getItemManager().getItemStack(id);
    }

    @Override
    public int getModelId(String id) {
        var stack = get(id);
        if (stack != null && stack.getItemMeta().hasCustomModelData()) {
            return stack.getItemMeta().getCustomModelData();
        }
        return -1;
    }

    @Override
    @Nullable
    public Color getColor(String id) {
        var stack = get(id);
        if (stack != null && stack.getItemMeta() instanceof LeatherArmorMeta meta) {
            return meta.getColor();
        }
        return null;
    }

}
