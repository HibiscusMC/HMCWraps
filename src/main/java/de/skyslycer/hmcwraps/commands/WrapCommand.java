package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.serialization.WrappableItem;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import java.util.Map;
import java.util.Objects;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.EntitySelector;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.command.CommandActor;

@Command("wraps")
public class WrapCommand {

    private final HMCWraps plugin;

    public WrapCommand(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onWraps(CommandActor actor) {
        var player = actor.as(BukkitActor.class).requirePlayer();
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            plugin.getHandler().send(player, Messages.NO_ITEM);
            return;
        }
        if (plugin.getCollection().getItems(item.getType()).isEmpty()) {
            plugin.getHandler().send(player, Messages.NO_WRAPS);
            return;
        }
        GuiBuilder.open(plugin, player, player.getInventory().getItemInMainHand(), EquipmentSlot.HAND);
    }

    @Subcommand("reload")
    @CommandPermission("hmcwraps.admin")
    public void onReload(CommandActor actor) {
        plugin.unload();
        plugin.load();
        plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_RELOAD);
    }

    @Subcommand("set")
    @CommandPermission("hmcwraps.admin")
    public void onSet(CommandSender sender, Player player, Wrap wrap) {
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType() == Material.AIR) {
            plugin.getHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        for (WrappableItem wrappableItem : plugin.getCollection().getItems(item.getType())) {
            if (wrappableItem.getWraps().containsValue(wrap)) {
                item = plugin.getWrapper().setWrap(wrap.getModelId(), wrap.getUuid(), item, false, player, true);
                item = plugin.getWrapper().setOwningPlayer(item, player.getUniqueId());
                player.getInventory().setItemInMainHand(item);
                plugin.getHandler().send(sender, Messages.COMMAND_WRAP_APPLIED);
                return;
            }
        }
        plugin.getHandler().send(sender, Messages.COMMAND_ITEM_NOT_FOR_WRAP);
    }

    @Subcommand("preview")
    @CommandPermission("hmcwraps.admin")
    public void onPreview(CommandSender sender, Player player, Wrap wrap) {
        var currentCollection = "";
        var itemMaterial = Material.AIR;
        for (Map.Entry<String, WrappableItem> entry : plugin.getConfiguration().getItems().entrySet()) {
            currentCollection = entry.getKey();
            if (entry.getValue().getWraps().containsValue(wrap)) {
                break;
            }
        }
        if (currentCollection.equals("")) {
            plugin.getHandler().send(sender, Messages.COMMAND_INVALID_WRAP);
            return;
        }

        if (Material.getMaterial(currentCollection) != null) {
            itemMaterial = Material.getMaterial(currentCollection);
        } else if (plugin.getCollection().getMaterials(currentCollection).stream().findFirst().isPresent()) {
            itemMaterial = plugin.getCollection().getMaterials(currentCollection).stream().findFirst().get();
        } else {
            plugin.getHandler().send(sender, Messages.COMMAND_NO_MATCHING_ITEM);
            return;
        }
        plugin.getPreviewManager().create(player, ItemBuilder.from(itemMaterial).model(wrap.getModelId()).build(), null);
        plugin.getHandler().send(sender, Messages.COMMAND_PREVIEW_CREATED);
    }

    @Subcommand("give wrapper")
    @CommandPermission("hmcwraps.admin")
    public void onGiveWrap(CommandActor actor, EntitySelector<Player> players, Wrap wrap, @Range(min = 1, max = 64) @Optional Integer amount) {
        if (wrap.getPhysical() == null) {
            plugin.getHandler()
                    .send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_INVALID_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
            return;
        } else {
            wrap.getPhysical().toItem(plugin, null);
        }
        players.forEach(player -> {
            var item = wrap.getPhysical().toItem(plugin, player);
            item.setAmount(amount == null ? 1 : amount);
            PlayerUtil.give(player, plugin.getWrapper().setWrapper(item, wrap.getUuid()));
        });
        plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_GIVEN_PHYSICAL,
                Placeholder.parsed("uuid", wrap.getUuid()));
    }

    @Subcommand("give unwrapper")
    @CommandPermission("hmcwraps.admin")
    public void onGiveUnwrapper(CommandActor actor, EntitySelector<Player> players, @Optional @Range(min = 1, max = 64) Integer amount) {
        players.forEach(player -> {
            var item = plugin.getConfiguration().getUnwrapper().toItem(plugin, player);
            item.setAmount(amount == null ? 1 : amount);
            PlayerUtil.give(player, plugin.getWrapper().setUnwrapper(item));
        });
        plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_GIVEN_UNWRAPPER);
    }

}
