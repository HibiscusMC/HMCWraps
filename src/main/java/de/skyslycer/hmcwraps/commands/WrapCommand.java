package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
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

    @Subcommand("give wrap")
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
            if (item == null) {
                plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_INVALID_REMOVER);
                return;
            }
            item.setAmount(amount == null ? 1 : amount);
            PlayerUtil.give(player, plugin.getWrapper().setUnwrapper(item));
        });
        plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_GIVEN_UNWRAPPER);
    }

}
