package de.skyslycer.hmcwraps.itemhook;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OraxenItemHook extends ItemHook {

    @Override
    public String getPrefix() {
        return "oraxen:";
    }

    @Override
    public ItemStack get(String id) {
        if (OraxenItems.getItemById(id) == null) {
            return null;
        }
        return OraxenItems.getItemById(id).build();
    }

    @Override
    @Nullable
    public String getTrimMaterial(String id) {
        return "minecraft:redstone";
    }

}
