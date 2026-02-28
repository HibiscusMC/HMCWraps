package de.skyslycer.hmcwraps.itemhook;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OraxenItemHook extends ItemHook {

    @Override
    public String getPrefix() {
        return "oraxen:";
    }

    @Nullable
    @Override
    public ItemStack get(String id) {
        if (OraxenItems.getItemById(id) == null) {
            return null;
        }
        return OraxenItems.getItemById(id).build();
    }

    @Nullable
    @Override
    public String get(ItemStack stack) {
        var item = OraxenItems.getIdByItem(stack);
        if (item == null) {
            return null;
        }
        return getPrefix() + item;
    }

    @Override
    @Nullable
    public String getTrimMaterial(String id) {
        return "minecraft:redstone";
    }

}
