package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemBreakEvent;

public class PlayerItemBreakListener implements Listener {

    private final HMCWraps plugin;

    public PlayerItemBreakListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemBreak(PlayerItemBreakEvent event) {
        if (!plugin.getConfiguration().getWrapping().isGiveWrapperAfterBreaking()) {
            return;
        }
        var wrap = plugin.getWrapper().getWrap(event.getBrokenItem());
        if (wrap == null || wrap.getPhysical() == null) {
            return;
        }
        PlayerUtil.give(event.getPlayer(), plugin.getWrapper()
                .setPhysicalWrapper(wrap.getPhysical().toItem(plugin, event.getPlayer()), wrap));
    }

}
