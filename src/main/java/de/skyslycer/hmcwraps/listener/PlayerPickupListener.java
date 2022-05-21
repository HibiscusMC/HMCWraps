package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.permission.PermissionHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PlayerPickupListener implements Listener {

    private final HMCWraps plugin;

    public PlayerPickupListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> PermissionHelper.loopThroughInventory(plugin, ((Player) event.getEntity()).getPlayer()), 1L);
    }

}
