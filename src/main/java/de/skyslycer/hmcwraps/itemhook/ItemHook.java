package de.skyslycer.hmcwraps.itemhook;

import org.bukkit.inventory.ItemStack;

public interface ItemHook {

    DefaultItemHook defaultHook = new DefaultItemHook();

    String getPrefix();

    ItemStack get(String id);

    int getModelId(String id);

}
