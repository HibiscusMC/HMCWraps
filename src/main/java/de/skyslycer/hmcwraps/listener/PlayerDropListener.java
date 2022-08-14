package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.permission.PermissionHelper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;

public class PlayerDropListener implements Listener {

    private final HMCWraps plugin;

    public PlayerDropListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDrop(EntityDropItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        var result = PermissionHelper.hasPermission(plugin, event.getItemDrop().getItemStack(), ((Player) event.getEntity()).getPlayer());
        if (result != null) {
            event.getItemDrop().setItemStack(result);
        }
    }

}
