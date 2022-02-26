package de.skyslycer.hmcwraps.itemhook;

import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.inventory.ItemStack;

public class OraxenItemHook implements ItemHook {

    @Override
    public String getPrefix() {
        return "oraxen:";
    }

    @Override
    public ItemStack get(String id) {
        return OraxenItems.getItemById(id).build();
    }

}
