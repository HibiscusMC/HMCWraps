package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.actions.information.WrapActionInformation;
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
import java.util.Map;

public class PlayerInteractListener implements Listener {

    private static final Map<Action, String> ACTIONS = Map.of(
            Action.LEFT_CLICK_AIR, "interact-left-air",
            Action.LEFT_CLICK_BLOCK, "interact-left-block",
            Action.RIGHT_CLICK_AIR, "interact-right-air",
            Action.RIGHT_CLICK_BLOCK, "interact-right-block"
    );

    private final HMCWrapsPlugin plugin;

    public PlayerInteractListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        var player = event.getPlayer();
        if (event.getAction() == Action.LEFT_CLICK_BLOCK && plugin.getPreviewManager().isPreviewing(player)) {
            plugin.getPreviewManager().remove(player.getUniqueId(), false);
        }
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

        runWrapActions(event);
        runWrapperActions(event);

        if (plugin.getWrapper().isGloballyDisabled(newItem)) {
            return;
        }

        var excludes = plugin.getConfiguration().getInventory().getShortcut().getExclude();
        var type = newItem.getType();
        if (plugin.getWrapper().getWrap(newItem) != null && !plugin.getWrapper().getModifiers().armorImitation().getOriginalMaterial(newItem).isEmpty()) {
            type = Material.valueOf(plugin.getWrapper().getModifiers().armorImitation().getOriginalMaterial(newItem));
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK
                || plugin.getCollectionHelper().getItems(type).isEmpty() || !player.isSneaking()
                || !plugin.getConfiguration().getInventory().getShortcut().isEnabled()
                || ListUtil.containsAny(List.of(type.toString(),
                player.getInventory().getItemInOffHand().getType().toString()), excludes)
                || (plugin.getConfiguration().getPermissions().isInventoryPermission()
                && !player.hasPermission(WrapCommand.WRAPS_PERMISSION))
                || (player.hasPermission("hmcwraps.shortcut.disable") && !player.isOp())) {
            return;
        }
        event.setCancelled(true);
        GuiBuilder.open(plugin, player, newItem, player.getInventory().getHeldItemSlot());
    }

    private void runWrapActions(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) return;
        var wrap = plugin.getWrapper().getWrap(item);
        if (wrap == null) return;
        if (wrap.getActions() == null) return;
        if (wrap.getActions().get("interact") != null) {
            plugin.getActionHandler().pushFromConfig(wrap.getActions().get("interact"), new WrapActionInformation(wrap, player, ""));;
        }
        if (wrap.getActions().get(ACTIONS.get(event.getAction())) != null) {
            plugin.getActionHandler().pushFromConfig(wrap.getActions().get(ACTIONS.get(event.getAction())), new WrapActionInformation(wrap, player, ""));
        }
    }

    private void runWrapperActions(PlayerInteractEvent event) {
        var player = event.getPlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) return;
        var wrapId = plugin.getWrapper().getPhysicalWrapper(item);
        if (wrapId == null) return;
        var wrap = plugin.getWrapsLoader().getWraps().get(wrapId);
        if (wrap == null) return;
        if (wrap.getPhysical() == null || wrap.getPhysical().getActions() == null) return;
        if (wrap.getPhysical().getActions().get("interact") != null) {
            plugin.getActionHandler().pushFromConfig(wrap.getPhysical().getActions().get("interact"), new WrapActionInformation(wrap, player, ""));
        }
        if (wrap.getPhysical().getActions().get(ACTIONS.get(event.getAction())) != null) {
            plugin.getActionHandler().pushFromConfig(wrap.getPhysical().getActions().get(ACTIONS.get(event.getAction())), new WrapActionInformation(wrap, player, ""));
        }
    }
}
