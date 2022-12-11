package de.skyslycer.hmcwraps.gui;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.inventory.IInventory;
import de.skyslycer.hmcwraps.serialization.inventory.InventoryAction;
import de.skyslycer.hmcwraps.serialization.inventory.InventoryType;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class GuiBuilder {

    public static void open(HMCWraps plugin, Player player, ItemStack item, EquipmentSlot slot) {
        plugin.getPreviewManager().remove(player.getUniqueId(), false);

        IInventory inventory = plugin.getConfiguration().getInventory();
        PaginatedGui gui;
        if (plugin.getConfiguration().getInventory().getType() == InventoryType.PAGINATED) {
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

        inventory.getItems().forEach((inventorySlot, serializableItem) -> {
            ItemStack stack = serializableItem.toItem(plugin, player);
            GuiItem guiItem = new GuiItem(stack);
            if (serializableItem.getAction() != null) {
                InventoryAction.add(guiItem, gui, serializableItem.getAction(),
                        plugin);
            }
            gui.setItem(inventorySlot, guiItem);
        });

        populate(plugin, item, slot, player, gui);

        gui.setDefaultClickAction(click -> click.setCancelled(true));
        gui.open(player);
    }

    private static void populate(HMCWraps plugin, ItemStack item, EquipmentSlot slot, Player player, PaginatedGui gui) {
        plugin.getCollectionHelper().getItems(item.getType()).forEach(it -> it.getWraps().forEach((ignored, wrap) -> {
            var wrapItem = wrap.toItem(plugin, player);
            wrapItem.setType(item.getType());

            GuiItem guiItem = new GuiItem(wrapItem);
            guiItem.setAction(click -> {
                if (click.getClick() == ClickType.LEFT) {
                    if (!wrap.hasPermission(player) && plugin.getConfiguration().getPermissionSettings().isPermissionVirtual()) {
                        plugin.getMessageHandler().send(player, Messages.NO_PERMISSION_FOR_WRAP);
                        return;
                    }
                    player.getInventory().setItem(slot, plugin.getWrapper().setWrap(wrap, item, false, player, true));
                    plugin.getMessageHandler().send(player, Messages.APPLY_WRAP);
                    plugin.getActionHandler().pushWrap(wrap, player);
                    player.getOpenInventory().close();
                } else if (click.getClick() == ClickType.RIGHT) {
                    if (!wrap.isPreview()) {
                        plugin.getMessageHandler().send(player, Messages.PREVIEW_DISABLED);
                        return;
                    }
                    plugin.getPreviewManager().create(player, guiItem.getItemStack(), gui);
                    plugin.getActionHandler().pushPreview(wrap, player);
                }
            });
            gui.addItem(guiItem);
        }));
        gui.setItem(plugin.getConfiguration().getInventory().getTargetItemSlot(), new GuiItem(item));
    }

}
