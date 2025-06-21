package de.skyslycer.hmcwraps.itemhook;

import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.inventory.ItemStack;

public class MythicItemHook extends ItemHook {

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

}
