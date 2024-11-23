package de.skyslycer.hmcwraps.gui;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.actions.information.ActionInformation;
import de.skyslycer.hmcwraps.actions.information.GuiActionInformation;
import de.skyslycer.hmcwraps.actions.information.WrapGuiActionInformation;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.util.MaterialUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiBuilder {

    public static void open(HMCWrapsPlugin plugin, Player player, ItemStack item, int slot) {
        plugin.getPreviewManager().remove(player.getUniqueId(), false);

        var inventory = plugin.getConfiguration().getInventory();
        var title = inventory.getTitle();
        if (item == null && inventory.getNoItemTitle() != null && !inventory.getNoItemTitle().isEmpty()) title = inventory.getNoItemTitle();
        PaginatedGui gui;
        if (plugin.getConfiguration().getInventory().getType() == Inventory.Type.PAGINATED) {
            gui = Gui.paginated()
                    .title(StringUtil.parseComponent(player, title))
                    .rows(inventory.getRows())
                    .create();
        } else {
            gui = Gui.scrolling().scrollType(ScrollType.VERTICAL)
                    .title(StringUtil.parseComponent(player, title))
                    .rows(inventory.getRows())
                    .create();
        }

        if (item != null) {
            populate(plugin, item, player, gui, slot);
        }
        populateStatic(plugin, player, inventory, gui, slot, item == null);
        if (slot != -1) {
            setItemToSlot(gui, plugin, player.getInventory().getItem(slot));
        }
        gui.setDefaultClickAction(click -> {
            click.setCancelled(true);
            if (click.getClickedInventory() == player.getInventory()) {
                var clicked = click.getCurrentItem();
                if (clicked == null || clicked.getType().isAir()) {
                    return;
                }
                var type = clicked.getType();
                if (plugin.getWrapper().getWrap(clicked) != null && !plugin.getWrapper().getOriginalData(clicked).material().isEmpty()) {
                    type = Material.valueOf(plugin.getWrapper().getOriginalData(clicked).material());
                }
                if (plugin.getCollectionHelper().getItems(type).isEmpty()) {
                    if (!plugin.getConfiguration().getInventory().isOpenWithoutItemEnabled()) {
                        plugin.getMessageHandler().send(player, Messages.NO_WRAPS);
                    }
                    return;
                }
                GuiBuilder.open(plugin, player, click.getCurrentItem(), click.getSlot());
            }
        });
        gui.setCloseGuiAction(close -> plugin.getWrapGui().remove(player.getUniqueId()));
        gui.open(player);
    }

    private static void populateStatic(HMCWrapsPlugin plugin, Player player, Inventory inventory, PaginatedGui gui, int slot, boolean noItem) {
        inventory.getItems().forEach((inventorySlot, serializableItem) -> {
            if (inventorySlot.endsWith("w") && noItem) return;
            if (inventorySlot.endsWith("n") && !noItem) return;
            var newInventorySlot = inventorySlot;
            if (Character.isAlphabetic(inventorySlot.charAt(inventorySlot.length() - 1))) {
                newInventorySlot = inventorySlot.substring(0, inventorySlot.length() - 1);
            }
            var fills = new ArrayList<Integer>();
            try {
                fills.add(Integer.parseInt(newInventorySlot));
            } catch (NumberFormatException exception) {
                plugin.getLogger().severe("Couldn't parse '" + newInventorySlot + "' in the inventory config as a valid numerical slot! Please change the value to a number.");
                return;
            }
            if (serializableItem.getFills() != null) {
                fills.addAll(serializableItem.getFills());
            }
            if (serializableItem.getId().equals("AIR") || serializableItem.getId().equals("EMPTY")) {
                gui.setItem(fills, new GuiItem(Material.AIR));
                return;
            }
            ItemStack stack = serializableItem.toItem(plugin, player);
            GuiItem guiItem = new GuiItem(stack);
            if (serializableItem.getActions() != null) {
                guiItem.setAction(event -> actions(plugin, new GuiActionInformation(player, "", gui, slot), serializableItem.getActions(), event));
            }
            gui.setItem(fills, guiItem);
        });
    }

    private static void actions(HMCWrapsPlugin plugin, ActionInformation information, HashMap<String, HashMap<String, List<String>>> actions, InventoryClickEvent event) {
        if (event.getClick() == ClickType.LEFT && actions.containsKey("left")) {
            plugin.getActionHandler().pushFromConfig(actions.get("left"), information);
        } else if (event.getClick() == ClickType.RIGHT && actions.containsKey("right")) {
            plugin.getActionHandler().pushFromConfig(actions.get("right"), information);
        } else if (event.getClick() == ClickType.MIDDLE && actions.containsKey("middle")) {
            plugin.getActionHandler().pushFromConfig(actions.get("middle"), information);
        }
        if (event.getClick() == ClickType.SHIFT_LEFT && actions.containsKey("left-shift")) {
            plugin.getActionHandler().pushFromConfig(actions.get("left-shift"), information);
        } else if (event.getClick() == ClickType.SHIFT_RIGHT && actions.containsKey("right-shift")) {
            plugin.getActionHandler().pushFromConfig(actions.get("right-shift"), information);
        }
        if (actions.containsKey("any-shift") && event.getClick().toString().contains("SHIFT")) {
            plugin.getActionHandler().pushFromConfig(actions.get("any-shift"), information);
        }
        if (actions.containsKey("any")) {
            plugin.getActionHandler().pushFromConfig(actions.get("any"), information);
        }
    }

    private static void populate(HMCWrapsPlugin plugin, ItemStack item, Player player, PaginatedGui gui, int slot) {
        var type = item.getType();
        if (plugin.getWrapper().getWrap(item) != null && !plugin.getWrapper().getOriginalData(item).material().isEmpty()) {
            type = Material.valueOf(plugin.getWrapper().getOriginalData(item).material());
        }
        var currentWrap = plugin.getWrapper().getWrap(item);
        if (currentWrap != null) {
            plugin.getWrapGui().put(player.getUniqueId(), currentWrap.getUuid());
        } else {
            plugin.getWrapGui().remove(player.getUniqueId());
        }

        List<WrapItemCombination> wrapItemCombinations = new ArrayList<>();
        plugin.getCollectionHelper().getItems(type).forEach(it -> it.getWraps()
                .values().stream().filter(wrap -> plugin.getWrapper().isValid(item, wrap))
                .filter(wrap -> !plugin.getFilterStorage().get(player) || wrap.hasPermission(player)).forEach(wrap -> {
                    wrapItemCombinations.add(new WrapItemCombination(wrap, wrap.toItem(plugin, player)));
                }));

        ItemComparator comparator = new ItemComparator(plugin.getConfiguration().getInventory(), player);
        wrapItemCombinations.sort(comparator);

        for (WrapItemCombination wrapItemCombination : wrapItemCombinations) {
            var wrap = wrapItemCombination.getWrap();
            if (currentWrap != null && currentWrap.getUuid().equals(wrap.getUuid()) && wrap.getEquippedItem() != null) {
                var equippedItem = new GuiItem(wrap.getEquippedItem().toItem(plugin, player));
                equippedItem.setAction(click -> {
                    if (wrap.getEquippedItem().getActions() != null) {
                        actions(plugin, new WrapGuiActionInformation(gui, wrap, player, slot, ""), wrap.getEquippedItem().getActions(), click);
                    }
                });
                gui.addItem(equippedItem);
                continue;
            }
            var wrapItem = wrap.toPermissionItem(plugin, MaterialUtil.getAlternative(wrap.getArmorImitationType(), type), player);
            var guiItem = new GuiItem(wrapItem);
            guiItem.setAction(click -> {
                if (!plugin.getConfiguration().getPermissions().isPermissionVirtual() || wrap.hasPermission(player)) {
                    if (plugin.getConfiguration().getInventory().getActions() != null) {
                        actions(plugin, new WrapGuiActionInformation(gui, wrap, player, slot, ""), plugin.getConfiguration().getInventory().getActions(), click);
                    }
                    if (wrap.getInventoryActions() != null) {
                        actions(plugin, new WrapGuiActionInformation(gui, wrap, player, slot, ""), wrap.getInventoryActions(), click);
                    }
                } else {
                    actions(plugin, new WrapGuiActionInformation(gui, wrap, player, slot, ""), plugin.getConfiguration().getInventory().getLockedActions(), click);
                    if (wrap.getLockedItem() != null && wrap.getLockedItem().getActions() != null) {
                        actions(plugin, new WrapGuiActionInformation(gui, wrap, player, slot, ""), wrap.getLockedItem().getActions(), click);
                    }
                }
            });
            gui.addItem(guiItem);
        }
    }

    private static void setItemToSlot(PaginatedGui gui, HMCWrapsPlugin plugin, ItemStack target) {
        if (target == null) return;
        var slot = plugin.getConfiguration().getInventory().getTargetItemSlot();
        if (slot != -1) {
            gui.setItem(slot, new GuiItem(target.clone()));
        }
    }

}
