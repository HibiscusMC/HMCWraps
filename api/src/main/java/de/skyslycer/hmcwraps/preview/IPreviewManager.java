package de.skyslycer.hmcwraps.preview;

import de.skyslycer.hmcwraps.serialization.IWrap;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

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
     * @param gui The GUI to open again
     * @param wrap The wrap to preview
     */
    void create(Player player, PaginatedGui gui, IWrap wrap);

    /**
     * Remove and stop all running previews.
     *
     * @param open If the inventory should open up again
     */
    void removeAll(boolean open);

}
