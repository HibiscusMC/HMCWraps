package de.skyslycer.hmcwraps.preview;

import de.skyslycer.hmcwraps.IHMCWraps;
import de.skyslycer.hmcwraps.events.ItemPreviewEvent;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PreviewManager {

    private final IHMCWraps plugin;

    private final Map<UUID, Preview> previews = new ConcurrentHashMap<>();

    public PreviewManager(IHMCWraps plugin) {
        this.plugin = plugin;
    }

    /**
     * Remove and stop a preview.
     *
     * @param uuid The UUID of the player
     * @param open If the inventory should open up again
     */
    public void remove(UUID uuid, boolean open) {
        if (previews.containsKey(uuid)) {
            previews.get(uuid).cancel(open);
            previews.remove(uuid);
        }
    }

    /**
     * Create a preview.
     *
     * @param player The player
     * @param gui The GUI to open again
     * @param wrap The wrap to preview
     */
    public void create(Player player, PaginatedGui gui, Wrap wrap) {
        var item = ItemBuilder.from(plugin.getCollectionHelper().getMaterial(wrap)).model(wrap.getModelId());
        if (wrap.getColor() != null) {
            item.color(wrap.getColor());
        }
        var event = new ItemPreviewEvent(player, item.build(), gui, wrap);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        createPrivate(event.getPlayer(), event.getItem(), event.getGui());
    }


    private void createPrivate(Player player, ItemStack item, PaginatedGui gui) {
        var preview = new Preview(player, item, gui, plugin);
        previews.put(player.getUniqueId(), preview);
        preview.preview();
    }

    /**
     * Remove and stop all running previews.
     *
     * @param open If the inventory should open up again
     */
    public void removeAll(boolean open) {
        previews.keySet().forEach(uuid -> this.remove(uuid, open));
    }

}
