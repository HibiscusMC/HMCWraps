package de.skyslycer.hmcwraps.itemhook;

import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface ItemHook {


    DefaultItemHook defaultHook = new DefaultItemHook();

    /**
     * Get the hook prefix.
     *
     * @return The hook prefix
     */
    String getPrefix();

    /**
     * Get an item stack based on the input.
     *
     * @param id The input
     * @return The item stack
     */
    @Nullable
    ItemStack get(String id);

    /**
     * Get the model id corresponding to the input.
     *
     * @param id The input
     * @return The model id
     */
    int getModelId(String id);

    /**
     * Get the color corresponding to the input.
     *
     * @param id The input
     * @return The color
     */
    @Nullable
    Color getColor(String id);

    /**
     * Get the trim pattern corresponding to the input.
     *
     * @param id The input
     * @return The trim pattern
     */
    @Nullable
    String getTrimPattern(String id);

    /**
     * Get the trim material corresponding to the input.
     *
     * @param id The input
     * @return The trim material
     */
    @Nullable
    String getTrimMaterial(String id);

}
