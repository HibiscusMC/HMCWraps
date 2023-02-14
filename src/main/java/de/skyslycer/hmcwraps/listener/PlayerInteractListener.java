package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.commands.WrapCommand;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerInteractListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        var newItem = PermissionUtil.hasPermission(plugin, event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer());
        if (newItem != null) {
            event.getPlayer().getInventory().setItemInMainHand(newItem);
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK
                || plugin.getCollectionHelper().getItems(event.getItem().getType()).isEmpty() || !event.getPlayer().isSneaking()
                || !plugin.getConfiguration().getInventory().getShortcut().isEnabled()
                || plugin.getConfiguration().getInventory().getShortcut().getExclude().contains(event.getItem().getType().toString())
                || (plugin.getConfiguration().getPermissions().isInventoryPermission()
                && !PermissionUtil.hasAnyPermission(event.getPlayer(), WrapCommand.WRAPS_PERMISSION))) {
            return;
        }
        event.setCancelled(true);
        GuiBuilder.open(plugin, event.getPlayer(), event.getItem());
    }

}
