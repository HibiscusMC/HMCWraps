package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.commands.WrapCommand;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.util.ListUtil;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class PlayerInteractListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerInteractListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType().isAir()) {
            return;
        }
        var currentItem = player.getInventory().getItemInMainHand();
        var newItem = PermissionUtil.check(plugin, player, currentItem);
        if (!currentItem.equals(newItem)) {
            player.getInventory().setItemInMainHand(newItem);
            if (newItem.getType().toString().contains("DISC") && event.getClickedBlock() != null && event.getClickedBlock().getType().equals(Material.JUKEBOX)) {
                event.setCancelled(true);
                return;
            }
        }

        var excludes = plugin.getConfiguration().getInventory().getShortcut().getExclude();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK
                || plugin.getCollectionHelper().getItems(newItem.getType()).isEmpty() || !player.isSneaking()
                || !plugin.getConfiguration().getInventory().getShortcut().isEnabled()
                || ListUtil.containsAny(List.of(newItem.getType().toString(),
                player.getInventory().getItemInOffHand().getType().toString()), excludes)
                || (plugin.getConfiguration().getPermissions().isInventoryPermission()
                && !player.hasPermission(WrapCommand.WRAPS_PERMISSION))) {
            return;
        }
        event.setCancelled(true);
        GuiBuilder.open(plugin, player, newItem);
    }

}
