package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.commands.WrapCommand;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.util.ListUtil;
import de.skyslycer.hmcwraps.util.PermissionUtil;
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
        if (event.getItem() == null) {
            return;
        }

        player.getInventory().setItemInMainHand(PermissionUtil.check(plugin, player, player.getInventory().getItemInMainHand()));

        var excludes = plugin.getConfiguration().getInventory().getShortcut().getExclude();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK
                || plugin.getCollectionHelper().getItems(event.getItem().getType()).isEmpty() || !player.isSneaking()
                || !plugin.getConfiguration().getInventory().getShortcut().isEnabled()
                || ListUtil.containsAny(List.of(player.getInventory().getItemInMainHand().getType().toString(),
                player.getInventory().getItemInOffHand().getType().toString()), excludes)
                || (plugin.getConfiguration().getPermissions().isInventoryPermission()
                && !player.hasPermission(WrapCommand.WRAPS_PERMISSION))) {
            return;
        }
        event.setCancelled(true);
        GuiBuilder.open(plugin, player, event.getItem());
    }

}
