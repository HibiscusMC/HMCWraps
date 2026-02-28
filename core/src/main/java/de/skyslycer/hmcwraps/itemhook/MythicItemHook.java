package de.skyslycer.hmcwraps.itemhook;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MythicItemHook extends ItemHook {

    @Override
    public String getPrefix() {
        return "mythic:";
    }

    @Nullable
    @Override
    public ItemStack get(String id) {
        if (MythicBukkit.inst().getItemManager().getItem(id).isEmpty()) {
            return null;
        }
        return MythicBukkit.inst().getItemManager().getItemStack(id);
    }

    @Nullable
    @Override
    public String get(ItemStack stack) {
        var item = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(stack);
        if (item == null) {
            return null;
        }
        return getPrefix() + item;
    }

}
