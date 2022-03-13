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
        plugin.getPreviewManager().remove(player.getUniqueId(), false);

        Inventory inventory = plugin.getConfiguration().getInventory();
        ScrollingGui gui = Gui.scrolling()
                .title(StringUtil.parseComponent(player, inventory.getTitle()))
                .rows(inventory.getRows())
                .pageSize((inventory.getRows() * 9) - inventory.getItems().size())
                .scrollType(ScrollType.HORIZONTAL)
                .create();

        inventory.getItems().forEach((inventorySlot, serializableItem) -> {
            ItemStack stack = serializableItem.toItem(plugin, player);
            if (stack != null) {
                GuiItem guiItem = new GuiItem(stack);
                if (serializableItem.getAction() != null) {
                    de.skyslycer.hmcwraps.serialization.inventory.Action.add(guiItem, gui, serializableItem.getAction(),
                            plugin);
                }
                gui.setItem(inventorySlot, guiItem);
            }
        });

        populate(plugin, item, slot, player, gui);

        gui.setDefaultClickAction(click -> click.setCancelled(true));
        gui.open(player);
    }

    private static void populate(HMCWraps plugin, ItemStack item, EquipmentSlot slot, Player player, ScrollingGui gui) {
        plugin.getConfiguration().getItems().get(item.getType().toString()).getWraps().forEach((ignored, wrap) -> {
            var builtItem = wrap.toItem(plugin, player);
            builtItem.setType(item.getType());
            var builder = ItemBuilder.from(builtItem);
            if (wrap.getLore() != null) {
                builder.lore(wrap.getLore().stream()
                        .map(it -> StringUtil.parseComponent(player, it, available(wrap, player, plugin)))
                        .collect(Collectors.toList()));
            }

            GuiItem guiItem = new GuiItem(builder.build());
            guiItem.setAction(click -> {
                if (click.getClick() == ClickType.LEFT) {
                    if (wrap.getPermission() != null && !player.hasPermission(wrap.getPermission())) {
                        plugin.getHandler().send(player, Messages.NO_PERMISSION_FOR_WRAP);
                        return;
                    }
                    player.getInventory().setItem(slot, plugin.getWrapper()
                            .setWrap(wrap.getModelId(), wrap.getUuid(), item, false,
                                    player));
                    plugin.getHandler().send(player, Messages.APPLY_WRAP);
                    player.getOpenInventory().close();
                } else if (click.getClick() == ClickType.RIGHT) {
                    if (wrap.isPreview() != null && !wrap.isPreview()) {
                        plugin.getHandler().send(player, Messages.PREVIEW_DISABLED);
                        return;
                    }
                    plugin.getPreviewManager().create(player, builder.build(), gui);
                }
            });
            gui.addItem(guiItem);
        });
        gui.setItem(plugin.getConfiguration().getInventory().getTargetItemSlot(), new GuiItem(item));
    }

    private static Single available(Wrap wrap, Player player, HMCWraps plugin) {
        if (wrap.getPermission() == null) {
            return Placeholder.parsed("available", plugin.getHandler().get(Messages.PLACEHOLDER_AVAILABLE));
        }
        return Placeholder.parsed("available",
                player.hasPermission(wrap.getPermission()) ? plugin.getHandler().get(Messages.PLACEHOLDER_AVAILABLE)
                        : plugin.getHandler().get(Messages.PLACEHOLDER_NOT_AVAILABLE));
    }

}
