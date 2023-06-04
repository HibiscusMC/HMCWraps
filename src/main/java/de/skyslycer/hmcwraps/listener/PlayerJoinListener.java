package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerJoinListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> PermissionUtil.loopThroughInventory(plugin, event.getPlayer()), 1);
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getUpdateChecker().checkPlayer(event.getPlayer()), 5);
    }

}
