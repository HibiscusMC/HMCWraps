package de.skyslycer.hmcwraps.preview;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.events.ItemPreviewEvent;
import de.skyslycer.hmcwraps.preview.floating.FloatingPreview;
import de.skyslycer.hmcwraps.preview.hand.HandPreview;
import de.skyslycer.hmcwraps.serialization.preview.PreviewType;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class PreviewManager {

    private final HMCWraps plugin;

    private final Map<UUID, Preview> previews = new ConcurrentHashMap<>();

    public PreviewManager(HMCWraps plugin) {
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
     * @param player  The player
     * @param onClose The consumer to run when the GUI should be opened again
     * @param wrap    The wrap to preview
     */
    public void create(Player player, Consumer<Player> onClose, Wrap wrap) {
        var item = ItemBuilder.from(plugin.getCollectionHelper().getMaterial(wrap)).model(wrap.getModelId());
        if (wrap.getColor() != null) {
            item.color(wrap.getColor());
        }
        var event = new ItemPreviewEvent(player, item.build(), onClose, wrap);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        createPrivate(event.getPlayer(), event.getItem(), event.getOnClose());
    }


    private void createPrivate(Player player, ItemStack item, Consumer<Player> onClose) {
        Preview preview;
        if (plugin.getConfiguration().getPreview().getType() == PreviewType.HAND) {
            preview = new HandPreview(player, item, onClose, plugin);
        } else {
            preview = new FloatingPreview(player, item, onClose, plugin);
        }
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

    /**
     * Check if a player is previewing.
     *
     * @param player The player
     * @return If the player is previewing
     */
    public boolean isPreviewing(Player player) {
        return previews.containsKey(player.getUniqueId());
    }

}
