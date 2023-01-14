package de.skyslycer.hmcwraps.preview;

import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface IPreviewManager {

    /**
     * Remove and stop a preview.
     *
     * @param uuid The UUID of the player
     * @param open If the inventory should open up again
     */
    void remove(UUID uuid, boolean open);

    /**
     * Create a preview.
     *
     * @param player The player
     * @param item   The item to preview
     * @param gui    The GUI to open again
     */
    void create(Player player, ItemStack item, PaginatedGui gui);

    /**
     * Remove and stop all running previews.
     *
     * @param open If the inventory should open up again
     */
    void removeAll(boolean open);

}
