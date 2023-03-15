package de.skyslycer.hmcwraps.itemhook;

import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Nullable;

public class OraxenItemHook implements ItemHook {

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
