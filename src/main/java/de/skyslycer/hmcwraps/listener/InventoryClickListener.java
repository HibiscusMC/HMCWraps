package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.preview.PreviewType;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public InventoryClickListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (event.getClickedInventory() != player.getInventory()) {
            return;
        }

        // Avoid possible issues such as client server inventory desync when moving a desynced inventory
        if (plugin.getPreviewManager().isPreviewing(player) && plugin.getConfiguration().getPreview().getType() == PreviewType.HAND) {
            plugin.getPreviewManager().remove(player.getUniqueId(), false);
            event.setCancelled(true);
            return;
        }

        switch (event.getAction()) {
            case PLACE_ALL, PLACE_SOME, PLACE_ONE -> {
                var slot = event.getRawSlot();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    var updatedItem = PermissionUtil.check(plugin, player, player.getInventory().getItem(slot));
                    if (updatedItem == null || updatedItem.equals(player.getInventory().getItem(slot))) return;
                    player.getInventory().setItem(slot, updatedItem);
                }, 1);
            }
        }

        if (event.getCursor() == null || event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) {
            return;
        }

        var target = event.getCurrentItem().clone();
        var physical = event.getCursor().clone();
        var cursor = physical.clone();
        if (cursor.getAmount() != 1) {
            cursor.setAmount(cursor.getAmount() - 1);
        } else {
            cursor = null;
        }

        if (plugin.getWrapper().isPhysicalUnwrapper(physical) && plugin.getWrapper().getWrap(target) != null) {
            event.setCurrentItem(plugin.getWrapper().removeWrap(target, player, true));
            var wrap = plugin.getWrapper().getWrap(target);
            plugin.getActionHandler().pushUnwrap(wrap, player);
            plugin.getActionHandler().pushPhysicalUnwrap(wrap, player);
            event.getWhoClicked().setItemOnCursor(cursor);
            event.setCancelled(true);
            return;
        }

        var wrapId = plugin.getWrapper().getPhysicalWrapper(physical);
        if (wrapId == null) {
            return;
        }

        var wrap = plugin.getWrapsLoader().getWraps().get(wrapId);
        if (wrap == null) {
            return;
        }
        var finalCursor = cursor;
        if (wrap.getPhysical() != null && (wrap.hasPermission(player) || !plugin.getConfiguration().getPermissions()
                .isPermissionPhysical())) {
            for (WrappableItem wrappableItem : plugin.getCollectionHelper().getItems(target.getType())) {
                if (wrappableItem.getWraps().containsValue(wrap)) {
                    if (!plugin.getConfiguration().getWrapping().getRewrap().isPhysicalEnabled() && plugin.getWrapper().getWrap(target) != null) {
                        plugin.getMessageHandler().send(player, Messages.NO_REWRAP);
                        return;
                    }
                    event.setCurrentItem(plugin.getWrapper().setWrap(wrap, target, true,
                            player, true));
                    plugin.getActionHandler().pushWrap(wrap, player);
                    plugin.getActionHandler().pushPhysicalWrap(wrap, player);
                    event.getWhoClicked().setItemOnCursor(finalCursor);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}
