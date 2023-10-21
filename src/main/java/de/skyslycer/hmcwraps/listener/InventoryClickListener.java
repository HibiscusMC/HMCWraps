package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.preview.PreviewType;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryClickListener implements Listener {

    private static final List<InventoryType> FORBIDDEN_INVENTORIES = List.of(
            InventoryType.ANVIL,
            InventoryType.WORKBENCH,
            InventoryType.ENCHANTING,
            InventoryType.GRINDSTONE,
            InventoryType.SMITHING
    );

    private final HMCWrapsPlugin plugin;

    public InventoryClickListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        if (isForbiddenInventory(event) && (isImitatedArmor(event.getCursor()) || isImitatedArmor(event.getCurrentItem()))) {
            event.setCancelled(true);
            plugin.getMessageHandler().send(player, Messages.ARMOR_IMITATION_FORBIDDEN_INVENTORY);
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
                    var updatedItem = PermissionUtil.check(plugin, player, event.getView().getItem(slot));
                    if (updatedItem == null || updatedItem.equals(event.getView().getItem(slot))) return;
                    event.getView().setItem(slot, updatedItem);
                }, 1);
            }
            case MOVE_TO_OTHER_INVENTORY -> Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (event.getClickedInventory() == player.getInventory()) {
                    PermissionUtil.loopThroughInventory(plugin, player, player.getOpenInventory().getTopInventory());
                } else {
                    PermissionUtil.loopThroughInventory(plugin, player, player.getOpenInventory().getBottomInventory());
                }
            }, 1);
        }

        if (event.getClickedInventory() != player.getInventory()) {
            return;
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
        var type = target.getType();
        if (plugin.getWrapper().getWrap(target) != null && !plugin.getWrapper().getOriginalData(target).material().isEmpty()) {
            type = Material.valueOf(plugin.getWrapper().getOriginalData(target).material());
        }
        if (wrap.getPhysical() != null && (wrap.hasPermission(player) || !plugin.getConfiguration().getPermissions()
                .isPermissionPhysical())) {
            for (WrappableItem wrappableItem : plugin.getCollectionHelper().getItems(type)) {
                if (wrappableItem.getWraps().containsValue(wrap)) {
                    if (!plugin.getConfiguration().getWrapping().getRewrap().isPhysicalEnabled() && plugin.getWrapper().getWrap(target) != null) {
                        plugin.getMessageHandler().send(player, Messages.NO_REWRAP);
                        return;
                    }
                    event.setCurrentItem(plugin.getWrapper().setWrap(wrap, target, true, player, true));
                    plugin.getActionHandler().pushWrap(wrap, player);
                    plugin.getActionHandler().pushPhysicalWrap(wrap, player);
                    event.getWhoClicked().setItemOnCursor(finalCursor);
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    private boolean isImitatedArmor(ItemStack item) {
        return item != null && !item.getType().isAir() && !plugin.getWrapper().getOriginalData(item).material().isBlank();
    }

    private boolean isForbiddenInventory(InventoryClickEvent event) {
        return FORBIDDEN_INVENTORIES.contains(event.getWhoClicked().getOpenInventory().getType()) || (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.CRAFTING);
    }

}
