package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

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

        if (event.getCurrentItem() != null) {
            var newItem = PermissionUtil.hasPermission(plugin, event.getCurrentItem(), player);
            if (newItem != null) {
                event.setCurrentItem(newItem);
            }
        }
        if (event.getCursor() != null) {
            var newItem = PermissionUtil.hasPermission(plugin, event.getCursor(), player);
            if (newItem != null) {
                event.setCursor(newItem);
            }
        }

        switch (event.getAction()) {
            case MOVE_TO_OTHER_INVENTORY -> {
                if (event.getClickedInventory() != event.getWhoClicked().getInventory() && event.getCurrentItem() != null) {
                    runFavorites(event.getCurrentItem().clone(), player);
                }
            }
            case PICKUP_SOME, PICKUP_ONE, PICKUP_HALF, PICKUP_ALL -> {
                if (event.getClickedInventory() != event.getWhoClicked().getInventory()) {
                    pickUp.add(player.getUniqueId());
                }
            }
            case PLACE_ALL, PLACE_ONE, PLACE_SOME, SWAP_WITH_CURSOR -> {
                if (event.getClickedInventory() == event.getWhoClicked().getInventory() && event.getCursor() != null &&
                        (pickUp.contains(player.getUniqueId()) || event.getAction() == InventoryAction.SWAP_WITH_CURSOR)) {
                    runFavorites(event.getCursor().clone(), player);
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

    private void runFavorites(ItemStack target, Player player) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> PermissionUtil.applyFavorites(plugin, player, target), 1L);
    }

}
