package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.actions.information.WrapActionInformation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerHitEntityListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerHitEntityListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        if (plugin.getPreviewManager().isPreviewing(player)) {
            plugin.getPreviewManager().remove(player.getUniqueId(), false);
        }
        var weapon = player.getInventory().getItemInMainHand();
        if (weapon == null || weapon.getType().isAir()) {
            return;
        }
        var wrap = plugin.getWrapper().getWrap(weapon);
        if (wrap == null || wrap.getActions() == null) {
            return;
        }
        if (wrap.getActions().get("hit-any") != null) {
            plugin.getActionHandler().pushFromConfig(wrap.getActions().get("hit-any"), new WrapActionInformation(wrap, player, ""));
        }
        if (event.getEntity() instanceof Player && wrap.getActions().get("hit-player") != null) {
            plugin.getActionHandler().pushFromConfig(wrap.getActions().get("hit-player"), new WrapActionInformation(wrap, player, ""));
        } else if (wrap.getActions().get("hit-entity") != null) {
            plugin.getActionHandler().pushFromConfig(wrap.getActions().get("hit-entity"), new WrapActionInformation(wrap, player, ""));
        }
    }

}
