package de.skyslycer.hmcwraps.itemhook;

import javax.annotation.Nullable;
import org.bukkit.inventory.ItemStack;

public interface ItemHook {


    DefaultItemHook defaultHook = new DefaultItemHook();

    /**
     * Get the hook prefix.
     * @return The hook prefix
     */
    String getPrefix();

    /**
     * Get an item stack based on the input.
     * @param id The input
     * @return The item stack
     */
    @Nullable
    ItemStack get(String id);

    /**
     * Get the model id corresponding to the input.
     * @param id The input
     * @return The model id
     */
    int getModelId(String id);

}
