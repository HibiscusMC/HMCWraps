package de.skyslycer.hmcwraps.itemhook;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.EquipmentSlot;
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
    public int getModelId(String id) {
        return -1;
    }

    @Override
    @Nullable
    public Color getColor(String id) {
        return null;
    }

    @Override
    @Nullable
    public String getTrimPattern(String id) {
        return null;
    }

    @Override
    @Nullable
    public String getTrimMaterial(String id) {
        return null;
    }

    @Override
    @Nullable
    public EquipmentSlot getEquippableSlot(String id) {
        return null;
    }

    @Override
    @Nullable
    public NamespacedKey getEquippableModel(String id) {
        return null;
    }

}
