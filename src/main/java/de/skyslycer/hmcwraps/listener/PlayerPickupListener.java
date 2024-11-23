package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PlayerPickupListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerPickupListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> PermissionUtil.loopThroughInventory(plugin, player, player.getInventory()), 1L);
    }

}
