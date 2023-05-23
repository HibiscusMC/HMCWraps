package de.skyslycer.hmcwraps.itemhook;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class DefaultItemHook implements ItemHook {

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
    public int getModelId(String id) {
        return -1;
    }

    @Override
    @Nullable
    public Color getColor(String id) {
        return null;
    }

}
