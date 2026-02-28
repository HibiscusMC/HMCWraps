package de.skyslycer.hmcwraps.itemhook;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DefaultItemHook extends ItemHook {

    @Override
    @Nullable
    public String getPrefix() {
        return null;
    }

    @Override
    @Nullable
    public ItemStack get(String id) {
        if (Material.getMaterial(id) != null) {
            return new ItemStack(Material.getMaterial(id));
        }
        return null;
    }

    @Override
    @Nullable
    public String get(ItemStack stack) {
        if (stack.hasItemMeta() && stack.getItemMeta().hasCustomModelData()) {
            return stack.getItemMeta().getCustomModelData() + "";
        }
        return null;
    }

}
