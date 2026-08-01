package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerPickupListener implements Listener {

    private final HMCWrapsPlugin plugin;
    private final Set<UUID> pendingInventoryChecks = ConcurrentHashMap.newKeySet();

    public PlayerPickupListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        var uuid = player.getUniqueId();
        if (!pendingInventoryChecks.add(uuid)) {
            return;
        }
        plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
            try {
                PermissionUtil.loopThroughInventory(plugin, player, player.getInventory());
            } finally { // remove even if exception is thrown
                pendingInventoryChecks.remove(uuid);
            }
        }, () -> pendingInventoryChecks.remove(uuid), 2L);
    }

}
