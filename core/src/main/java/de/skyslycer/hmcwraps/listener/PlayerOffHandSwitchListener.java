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
        var offHand = event.getOffHandItem();
        var updatedOffHand = PermissionUtil.check(plugin, event.getPlayer(), offHand);
        if (updatedOffHand != null && !updatedOffHand.equals(offHand)) {
            event.setOffHandItem(updatedOffHand);
        }
        var mainHand = event.getMainHandItem();
        var updatedMainHand = PermissionUtil.check(plugin, event.getPlayer(), mainHand);
        if (updatedMainHand != null && !updatedMainHand.equals(mainHand)) {
            event.setMainHandItem(updatedMainHand);
        }
    }

}
