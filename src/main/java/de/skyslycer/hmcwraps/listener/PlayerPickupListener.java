package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.PermissionUtil;
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
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            PermissionUtil.loopThroughInventory(plugin, player);
            PermissionUtil.applyFavorites(plugin, player, event.getItem().getItemStack());
        }, 1L);
    }

}
