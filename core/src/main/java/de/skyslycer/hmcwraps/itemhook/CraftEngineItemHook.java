package de.skyslycer.hmcwraps.itemhook;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemManager;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CraftEngineItemHook extends ItemHook {

    @Override
    public String getPrefix() {
        return "craftengine:";
    }

    @Nullable
    @Override
    public ItemStack get(String id) {
        var item = CraftEngineItems.byId(Key.of(id));
        if (item == null) return null;
        var stack = item.buildItemStack();
        var optionalClientBound = BukkitItemManager.instance().s2c(stack, null);
        return optionalClientBound.orElse(stack);
    }

    @Nullable
    @Override
    public String get(ItemStack stack) {
        var item = CraftEngineItems.byItemStack(stack);
        if (item == null) return null;
        return getPrefix() + item.id().toString();
    }

}
