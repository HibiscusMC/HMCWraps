package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerOffHandSwitchListener implements Listener {

    private final HMCWraps plugin;

    public PlayerOffHandSwitchListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSwitch(PlayerSwapHandItemsEvent event) {
        var offHand = PermissionUtil.hasPermission(plugin, event.getOffHandItem(), event.getPlayer());
        var mainHand = PermissionUtil.hasPermission(plugin, event.getMainHandItem(), event.getPlayer());
        if (offHand != null) {
            event.setOffHandItem(offHand);
        }
        if (mainHand != null) {
            event.setMainHandItem(mainHand);
        }
    }

}
