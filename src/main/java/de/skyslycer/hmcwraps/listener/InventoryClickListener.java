package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
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

        switch (event.getAction()) {
            case PLACE_ALL, PLACE_SOME, PLACE_ONE -> {
                var slot = event.getRawSlot();
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    var item = player.getInventory().getItem(slot);
                    if (item == null || item.getType().isAir()) {
                        return;
                    }
                    var newItem = PermissionUtil.hasPermission(plugin, item, player);
                    if (newItem != null || plugin.getWrapper().getWrap(event.getCursor()) == null) {
                        var favoriteItem = PermissionUtil.applyFavorite(plugin, player, item);
                        if (favoriteItem != null) {
                            player.getInventory().setItem(slot, favoriteItem);
                        } else if (newItem != null) {
                            player.getInventory().setItem(slot, newItem);
                        }
                    }
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
