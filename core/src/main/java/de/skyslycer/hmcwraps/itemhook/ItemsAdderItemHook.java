package de.skyslycer.hmcwraps.itemhook;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;


public class ItemsAdderItemHook extends ItemHook {

    @Override
    public String getPrefix() {
        return "itemsadder:";
    }

    @Override
    public ItemStack get(String id) {
        final CustomStack stack = CustomStack.getInstance(id);
        if (stack == null) {
            return null;
        }
        return stack.getItemStack().clone();
    }

}
