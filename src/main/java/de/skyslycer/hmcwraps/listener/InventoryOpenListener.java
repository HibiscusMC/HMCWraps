package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class InventoryOpenListener implements Listener {

    private final HMCWraps plugin;

    public InventoryOpenListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> PermissionUtil.loopThroughInventory(plugin, (Player) event.getPlayer()), 1);
    }

}
