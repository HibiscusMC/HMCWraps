package de.skyslycer.hmcwraps.itemhook;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;


public class ItemsAdderItemHook extends ItemHook {

    @Override
    public String getPrefix() {
        return "itemsadder:";
    }

    @Nullable
    @Override
    public ItemStack get(String id) {
        var stack = CustomStack.getInstance(id);
        if (stack == null) {
            return null;
        }
        return stack.getItemStack().clone();
    }

    @Nullable
    @Override
    public String get(ItemStack stack) {
        var item = CustomStack.byItemStack(stack);
        if (item == null) {
            return null;
        }
        return getPrefix() + item.getId();
    }

}
