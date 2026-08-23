package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class PlayerDropListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerDropListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (plugin.getPreviewManager().isPreviewing(event.getPlayer())) {
            plugin.getPreviewManager().remove(event.getPlayer().getUniqueId(), false);
        }
        var item = event.getItemDrop().getItemStack();
        var result = PermissionUtil.check(plugin, event.getPlayer(), item);
        if (result != null && !result.equals(item)) {
            event.getItemDrop().setItemStack(result);
        }
    }

}
