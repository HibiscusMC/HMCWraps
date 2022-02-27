package de.skyslycer.hmcwraps.gui;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import dev.triumphteam.gui.guis.ScrollingGui;
import java.util.stream.Collectors;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class GuiBuilder {

    public static void open(HMCWraps plugin, Player player, ItemStack item, EquipmentSlot slot) {
        if (item == null) {
            plugin.getHandler().send(player, Messages.NO_ITEM);
            return;
        }

        if (!plugin.getConfiguration().getItems().containsKey(item.getType().toString())) {
            plugin.getHandler().send(player, Messages.NO_WRAPS);
            return;
        }

        Inventory inventory = plugin.getConfiguration().getInventory();
        ScrollingGui gui = Gui.scrolling()
                .title(StringUtil.parse(inventory.getTitle()))
                .rows(inventory.getRows())
                .pageSize((inventory.getRows() * 9) - inventory.getItems().size())
                .scrollType(ScrollType.HORIZONTAL)
                .create();

        inventory.getItems().forEach((inventorySlot, serializableItem) -> {
            ItemStack stack = serializableItem.toItem(plugin);
            if (stack != null) {
                GuiItem guiItem = new GuiItem(stack);
                if (serializableItem.getAction() != null) {
                    de.skyslycer.hmcwraps.serialization.inventory.Action.add(guiItem, gui, serializableItem.getAction(), plugin);
                }
                gui.setItem(inventorySlot, guiItem);
            }
        });

        populate(plugin, item, slot, player, gui);

        gui.setDefaultClickAction(click -> click.setCancelled(true));
        gui.open(player);
    }

    private static void populate(HMCWraps plugin, ItemStack item, EquipmentSlot slot, Player player, ScrollingGui gui) {
        plugin.getConfiguration().getItems().get(item.getType().toString()).getWraps()
                .forEach((ignored, wrap) -> {
                    int id;
                    try {
                        id = Integer.parseInt(wrap.getId());
                    } catch (NumberFormatException exception) {
                        id = plugin.getModellIdFromHook(wrap.getId());
                    }
                    var stack = ItemBuilder.from(item.getType())
                            .name(StringUtil.parse(wrap.getName(), available(wrap, player, plugin)))
                            .lore(wrap.getLore().stream()
                                    .map(line -> StringUtil.parse(line, available(wrap, player, plugin)))
                                    .collect(Collectors.toList())).glow(wrap.isGlow());
                    if (id != -1) {
                        stack.model(id);
                    }
                    GuiItem guiItem = new GuiItem(stack.build());
                    guiItem.setAction(click -> {
                        if (click.getClick() == ClickType.LEFT) {
                            if (!player.hasPermission(wrap.getPermission())) {
                                plugin.getHandler().send(player, Messages.NO_PERMISSION_FOR_WRAP);
                                return;
                            }
                            player.getInventory().setItem(slot, plugin.getWrapper()
                                    .setWrap(plugin.getModellIdFromHook(wrap.getId()), wrap.getUuid(), item, false, player));
                        } else if (click.getClick() == ClickType.RIGHT) {
                            // TODO: do preview
                        }
                    });
                    gui.addItem(guiItem);
                });
    }

    private static Single available(Wrap wrap, Player player, HMCWraps plugin) {
        return Placeholder.parsed("%available%",
                player.hasPermission(wrap.getPermission()) ? plugin.getHandler().get(Messages.PLACEHOLDER_AVAILABLE)
                        : plugin.getHandler().get(Messages.PLACEHOLDER_NOT_AVAILABLE));
    }

}
