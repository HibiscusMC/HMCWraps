package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InventoryClickListener implements Listener {

    private final HMCWrapsPlugin plugin;

    private final Set<UUID> pickUp = new HashSet<>();

    public InventoryClickListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            var newItem = PermissionUtil.hasPermission(plugin, event.getCurrentItem(), player);
            if (newItem != null || plugin.getWrapper().getWrap(event.getCurrentItem()) == null) {
                var favoriteItem = PermissionUtil.applyFavorite(plugin, player, event.getCurrentItem());
                if (favoriteItem != null) {
                    event.setCurrentItem(favoriteItem);
                } else if (newItem != null) {
                    event.setCurrentItem(newItem);
                }
            }
        }
        if (event.getCursor() != null && event.getCursor().getType() != Material.AIR) {
            var newItem = PermissionUtil.hasPermission(plugin, event.getCursor(), player);
            if (newItem != null || plugin.getWrapper().getWrap(event.getCursor()) == null) {
                var favoriteItem = PermissionUtil.applyFavorite(plugin, player, event.getCursor());
                if (favoriteItem != null) {
                    event.setCursor(favoriteItem);
                } else if (newItem != null) {
                    event.setCursor(newItem);
                }
            }
        }

        if (event.getCursor() == null || event.getClickedInventory() != event.getWhoClicked().getInventory() || event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) {
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
