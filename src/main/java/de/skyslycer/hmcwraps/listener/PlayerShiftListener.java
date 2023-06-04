package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerShiftListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerShiftListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent event) {
        if (event.isSneaking() && plugin.getConfiguration().getPreview().getSneakCancel().isEnabled()) {
            plugin.getPreviewManager().remove(event.getPlayer().getUniqueId(), true);
        }
    }

}
