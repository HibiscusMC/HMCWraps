package de.skyslycer.hmcwraps.itemhook;

import com.nexomc.nexo.api.NexoItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NexoItemHook extends ItemHook {

    @Override
    public String getPrefix() {
        return "nexo:";
    }

    @Nullable
    @Override
    public ItemStack get(String id) {
        if (NexoItems.itemFromId(id) == null) {
            return null;
        }
        return NexoItems.itemFromId(id).build();
    }

    @Nullable
    @Override
    public String get(ItemStack stack) {
        return "";
    }

}
