package de.skyslycer.hmcwraps.listener;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

    private final HMCWraps plugin;

    public PlayerInteractListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR & event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getItem() == null || !event.getPlayer().isSneaking()) {
            return;
        }

        event.setCancelled(true);

        if (!plugin.getConfiguration().getItems().containsKey(event.getItem().getType().toString())) {
            plugin.getHandler().send(event.getPlayer(), Messages.NO_WRAPS);
            return;
        }

        Inventory inventory = plugin.getConfiguration().getInventory();
        ScrollingGui gui = Gui.scrolling()
                .title(StringUtil.parse(inventory.getTitle()))
                .rows(inventory.getRows())
                .pageSize((inventory.getRows() * 9) - inventory.getItems().size())
                .scrollType(ScrollType.HORIZONTAL)
                .create();

        inventory.getItems().forEach((slot, item) -> {
            ItemStack stack = item.toItem(plugin);
            if (stack != null) {
                GuiItem guiItem = new GuiItem(stack);
                if (item.getAction() != null) {
                    de.skyslycer.hmcwraps.serialization.inventory.Action.add(guiItem, gui, item.getAction());
                }
                gui.setItem(slot, guiItem);
            }
        });

        plugin.getConfiguration().getItems().get(event.getItem().getType().toString()).getWraps()
                .forEach((ignored, wrap) -> {
                    int id;
                    try {
                        id = Integer.parseInt(wrap.getId());
                    } catch (NumberFormatException exception) {
                        id = plugin.getModellIdFromHook(wrap.getId());
                    }
                    var stack = ItemBuilder.from(event.getItem().getType()).name(StringUtil.parse(wrap.getName(), available(wrap, event.getPlayer())))
                            .lore(wrap.getLore().stream().map(line -> StringUtil.parse(line, available(wrap, event.getPlayer()))).collect(Collectors.toList())).glow(wrap.isGlow());
                    if (id != -1) {
                        stack.model(id);
                    }
                    gui.addItem(new GuiItem(stack.build())); // TODO: add applying wraps
                });

        gui.open(event.getPlayer());
    }

    private Single available(Wrap wrap, Player player) {
        return Placeholder.parsed("%available%", player.hasPermission(wrap.getPermission()) ? plugin.getHandler().get(Messages.PLACEHOLDER_AVAILABLE) : plugin.getHandler().get(Messages.PLACEHOLDER_NOT_AVAILABLE));
    }

}
