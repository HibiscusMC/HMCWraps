package de.skyslycer.hmcwraps.serialization;

import org.bukkit.Color;

public interface IWrapValues {

    /**
     * Get the model id of the wrap value.
     *
     * @return The model id
     */
    int getModelId();

    /**
     * Get the color of the wrap value.
     *
     * @return The color
     */
    Color getColor();

}
