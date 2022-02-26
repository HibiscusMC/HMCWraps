package de.skyslycer.hmcwraps.itemhook;

import dev.lone.itemsadder.api.ItemsAdder;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderItemHook implements ItemHook {

    @Override
    public String getPrefix() {
        return "itemsadder:";
    }

    @Override
    public ItemStack get(String id) {
        try {
            return ItemsAdder.getAllItems(id).stream().findFirst().get().getItemStack();
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public int getModellId(String id) {
        var stack = get(id);
        if (stack != null && stack.getItemMeta().hasCustomModelData()) {
            return stack.getItemMeta().getCustomModelData();
        }
        return -1;
    }

}
