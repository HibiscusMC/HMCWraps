package de.skyslycer.hmcwraps.gui;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.actions.information.ActionInformation;
import de.skyslycer.hmcwraps.actions.information.GuiActionInformation;
import de.skyslycer.hmcwraps.actions.information.WrapGuiActionInformation;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiBuilder {

    private static final String MAGIC_IDENTIFIER = "Sn6sma";
    private static final int RANDOM_MODEL_ID = Integer.MAX_VALUE - 7239462;

    public static void open(HMCWrapsPlugin plugin, Player player, ItemStack item) {
        plugin.getPreviewManager().remove(player.getUniqueId(), false);

        var inventory = plugin.getConfiguration().getInventory();
        PaginatedGui gui;
        if (plugin.getConfiguration().getInventory().getType() == Inventory.Type.PAGINATED) {
            gui = Gui.paginated()
                    .title(StringUtil.parseComponent(player, inventory.getTitle()))
                    .rows(inventory.getRows())
                    .pageSize((inventory.getRows() * 9) - inventory.getItems().size())
                    .create();
        } else {
            gui = Gui.scrolling().scrollType(ScrollType.VERTICAL)
                    .title(StringUtil.parseComponent(player, inventory.getTitle()))
                    .rows(inventory.getRows())
                    .pageSize((inventory.getRows() * 9) - inventory.getItems().size())
                    .create();
        }

        populateStatic(plugin, player, inventory, gui);
        populate(plugin, item, player, gui);
        setItemToSlot(gui, plugin, item);

        gui.setDefaultClickAction(click -> click.setCancelled(true));
        gui.open(player);
        for (int i = 0; i < gui.getRows() * 9; i++) {
            var playerInventory = player.getOpenInventory().getTopInventory();
            if (isEmptyItem(playerInventory.getItem(i))) {
                playerInventory.setItem(i, new ItemStack(Material.AIR));
            }
        }
    }

    private static void populateStatic(HMCWrapsPlugin plugin, Player player, Inventory inventory, PaginatedGui gui) {
        inventory.getItems().forEach((inventorySlot, serializableItem) -> {
            var fills = new ArrayList<Integer>();
            fills.add(inventorySlot);
            if (serializableItem.getFills() != null) {
                fills.addAll(serializableItem.getFills());
            }
            if (serializableItem.getId().equals("AIR") || serializableItem.getId().equals("EMPTY")) {
                gui.setItem(fills, getEmptyItem());
                return;
            }
            ItemStack stack = serializableItem.toItem(plugin, player);
            GuiItem guiItem = new GuiItem(stack);
            if (serializableItem.getActions() != null) {
                guiItem.setAction(event -> actions(plugin, new GuiActionInformation(player, "", gui), serializableItem.getActions(), event));
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
        if (!actions.containsKey("any")) {
            return;
        }
        plugin.getActionHandler().pushFromConfig(actions.get("any"), information);
    }

    private static void populate(HMCWrapsPlugin plugin, ItemStack item, Player player, PaginatedGui gui) {
        plugin.getCollectionHelper().getItems(item.getType()).forEach(it -> it.getWraps()
                .values().stream().filter(wrap -> plugin.getWrapper().isValid(item, wrap))
                .filter(wrap -> !plugin.getFilterStorage().get(player) || wrap.hasPermission(player)).forEach(wrap -> {
                    var wrapItem = wrap.toPermissionItem(plugin, player);
                    if (!plugin.getConfiguration().getPermissions().isPermissionVirtual() || wrap.hasPermission(player) || wrap.getLockedItem() == null) {
                        wrapItem.setType(item.getType());
                    }

                    var guiItem = new GuiItem(wrapItem);
                    guiItem.setAction(click -> {
                        if (plugin.getConfiguration().getInventory().getActions() != null) {
                            actions(plugin, new WrapGuiActionInformation(gui, wrap, player, ""), plugin.getConfiguration().getInventory().getActions(), click);
                        }
                    });
                    gui.addItem(guiItem);
                }));
    }

    private static GuiItem getEmptyItem() {
        return new GuiItem(ItemBuilder.from(Material.CAVE_SPIDER_SPAWN_EGG).name(Component.text(MAGIC_IDENTIFIER)).model(RANDOM_MODEL_ID).build());
    }

    private static boolean isEmptyItem(ItemStack item) {
        return item != null && item.hasItemMeta() && item.getItemMeta().hasDisplayName() &&
                item.getItemMeta().getDisplayName().equals(MAGIC_IDENTIFIER) && item.getItemMeta().hasCustomModelData()
                && item.getItemMeta().getCustomModelData() == RANDOM_MODEL_ID;
    }

    private static void setItemToSlot(PaginatedGui gui, HMCWrapsPlugin plugin, ItemStack target) {
        var slot = plugin.getConfiguration().getInventory().getTargetItemSlot();
        if (slot != -1) {
            gui.setItem(slot, new GuiItem(target.clone()));
        }
    }

}
