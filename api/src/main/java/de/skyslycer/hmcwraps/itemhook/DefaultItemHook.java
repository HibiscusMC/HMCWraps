package de.skyslycer.hmcwraps.itemhook;

import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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

}
