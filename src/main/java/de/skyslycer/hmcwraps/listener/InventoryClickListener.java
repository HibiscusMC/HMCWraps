package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.preview.PreviewType;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import de.skyslycer.hmcwraps.util.VersionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryClickListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public InventoryClickListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        var player = (Player) event.getWhoClicked();

        var cancel = false;
        if ((isImitatedArmor(event.getCursor()) || isImitatedArmor(event.getCurrentItem()))
                && isForbiddenInventory(event, List.of(InventoryType.ENCHANTING, InventoryType.GRINDSTONE, InventoryType.ANVIL))) {
            cancel = true;
        }
        if ((isFakeDurability(event.getCursor()) || isFakeDurability(event.getCurrentItem()))
                && isForbiddenInventory(event, List.of(InventoryType.ANVIL, InventoryType.GRINDSTONE))) {
            cancel = true;
        }
        if ((isTrimsUsed(event.getCursor()) || isTrimsUsed(event.getCurrentItem()))
                && isForbiddenInventory(event, List.of(InventoryType.SMITHING))) {
            cancel = true;
        }
        if (cancel) {
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
            case PLACE_ALL, PLACE_SOME, PLACE_ONE, SWAP_WITH_CURSOR -> {
                var slot = event.getRawSlot();
                plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
                    var updatedItem = PermissionUtil.check(plugin, player, VersionUtil.getItemFromSlot(event, slot));
                    if (updatedItem == null || updatedItem.equals(VersionUtil.getItemFromSlot(event, slot))) return;
                    VersionUtil.setItemInSlot(event, slot, updatedItem);
                }, 1);
            }
            case MOVE_TO_OTHER_INVENTORY -> plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
                if (event.getClickedInventory() == player.getInventory()) {
                    PermissionUtil.loopThroughInventory(plugin, player, VersionUtil.getTopInventory(player));
                } else {
                    PermissionUtil.loopThroughInventory(plugin, player, VersionUtil.getBottomInventory(player));
                }
            }, 1);
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            var slot = event.getHotbarButton();
            plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
                var updatedItem = PermissionUtil.check(plugin, player, player.getInventory().getItem(slot));
                if (updatedItem == null || updatedItem.equals(VersionUtil.getItemFromSlot(event, slot))) return;
                player.getInventory().setItem(slot, updatedItem);
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
            event.setCurrentItem(plugin.getWrapper().removeWrap(target, player));
            var wrap = plugin.getWrapper().getWrap(target);
            plugin.getActionHandler().pushUnwrap(wrap, player);
            plugin.getActionHandler().pushPhysicalUnwrap(wrap, player);
            event.getWhoClicked().setItemOnCursor(cursor);
            event.setCancelled(true);
            return;
        }

        if (plugin.getWrapper().isGloballyDisabled(target)) {
            return;
        }

        var wrapId = plugin.getWrapper().getPhysicalWrapper(physical);
        if (wrapId == null) {
            return;
        }

        var wrap = plugin.getWrapsLoader().getWraps().get(wrapId);
        if (wrap == null || !plugin.getWrapper().isValid(target, wrap)) {
            return;
        }

        var finalCursor = cursor;
        var type = target.getType();
        if (plugin.getWrapper().getWrap(target) != null && !plugin.getWrapper().getModifiers().armorImitation().getOriginalMaterial(target).isEmpty()) {
            type = Material.valueOf(plugin.getWrapper().getModifiers().armorImitation().getOriginalMaterial(target));
        }
        var currentWrap = plugin.getWrapper().getWrap(target);
        if (wrap.getPhysical() != null && (wrap.hasPermission(player) || !plugin.getConfiguration().getPermissions()
                .isPermissionPhysical())) {
            var anyMatch = plugin.getCollectionHelper().getItems(type).stream().anyMatch(wrap::equals);
            if (anyMatch) {
                if (!plugin.getConfiguration().getWrapping().getRewrap().isPhysicalEnabled() && currentWrap != null) {
                    plugin.getMessageHandler().send(player, Messages.NO_REWRAP);
                    return;
                }
                if (currentWrap != null && currentWrap.getUuid().equals(wrap.getUuid()) && !plugin.getConfiguration().getWrapping().getRewrap().isSamePhysicalEnabled()) {
                    return;
                }
                event.setCurrentItem(plugin.getWrapper().setWrap(wrap, target, true, player));
                plugin.getActionHandler().pushWrap(wrap, player);
                plugin.getActionHandler().pushPhysicalWrap(wrap, player);
                event.getWhoClicked().setItemOnCursor(finalCursor);
                event.setCancelled(true);
            }
        }
    }

    private boolean isImitatedArmor(ItemStack item) {
        return item != null && !item.getType().isAir() && plugin.getWrapper().getWrap(item) != null &&
                !plugin.getWrapper().getModifiers().armorImitation().getOriginalMaterial(item).isBlank();
    }

    private boolean isFakeDurability(ItemStack item) {
        return item != null && !item.getType().isAir() && plugin.getWrapper().getWrap(item) != null &&
                plugin.getWrapper().getModifiers().armorImitation().getFakeDurability(item) != -1;
    }

    private boolean isTrimsUsed(ItemStack item) {
        return item != null && !item.getType().isAir() && plugin.getWrapper().getWrap(item) != null &&
                plugin.getWrapper().getModifiers().trim().isTrimsUsed(item);
    }

    private boolean isForbiddenInventory(InventoryClickEvent event, List<InventoryType> forbiddenInventories) {
        var inventoryType = VersionUtil.getOpenInventoryType((Player) event.getWhoClicked());
        var clickedInventoryType = event.getClickedInventory() == null ? InventoryType.PLAYER : event.getClickedInventory().getType(); // Use PLAYER if null to avoid NPE
        if (forbiddenInventories.contains(clickedInventoryType)) {
            return true;
        }
        return (forbiddenInventories.contains(inventoryType)) && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY;
    }

}
