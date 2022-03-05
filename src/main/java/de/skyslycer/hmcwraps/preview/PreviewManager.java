package de.skyslycer.hmcwraps.preview;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PreviewManager {

    private final HMCWraps plugin;

    private final Map<UUID, Preview> previews = new ConcurrentHashMap<>();

    public PreviewManager(HMCWraps plugin) {
        this.plugin = plugin;
    }

    public void remove(UUID uuid, boolean open) {
        if (previews.containsKey(uuid)) {
            previews.get(uuid).cancel(open);
            previews.remove(uuid);
        }
    }

    public void create(Player player, ItemStack item, PaginatedGui gui) {
        var location = PlayerUtil.getLookBlock(player);
        if (location == null) {
            plugin.getHandler().send(player, Messages.PREVIEW_NOT_ENOUGH_SPACE);
            return;
        }
        var preview = new Preview(player, item, gui, plugin);
        previews.put(player.getUniqueId(), preview);
        preview.preview();
    }

    public void removeAll(boolean open) {
        previews.keySet().forEach(uuid -> this.remove(uuid, open));
    }

}
