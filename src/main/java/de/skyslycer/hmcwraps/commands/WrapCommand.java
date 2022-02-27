package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Dependency;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.bukkit.EntitySelector;
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
        GuiBuilder.open(plugin, player, player.getInventory().getItemInMainHand(), EquipmentSlot.HAND);
    }

    @Subcommand("reload")
    public void onReload(CommandActor actor) {
        plugin.unload();
        plugin.load();
        plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_RELOAD);
    }

    @Subcommand("give wrap")
    public void onGiveWrap(CommandActor actor, EntitySelector<Player> players, Wrap wrap, @Optional @Range(min = 1, max = 64) Integer amount) {
        if (wrap.getPhysical() == null || wrap.getPhysical().toItem(plugin) == null) {
            plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_INVALID_PHYSICAL,
                    Placeholder.parsed("%uuid%", wrap.getUuid()));
            return;
        }
        var item = wrap.getPhysical().toItem(plugin);
        item.setAmount(amount == null ? 1 : amount);
        players.forEach(player -> PlayerUtil.give(player, item));
        plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_GIVEN_PHYSICAL,
                Placeholder.parsed("%uuid%", wrap.getUuid()));
    }

    @Subcommand("give unwrapper")
    public void onGiveUnwrapper(CommandActor actor, EntitySelector<Player> players, @Optional @Range(min = 1, max = 64) Integer amount) {
        var item = plugin.getConfiguration().getUnwrapper().toItem(plugin);
        if (item == null) {
            plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_INVALID_REMOVER);
            return;
        }
        item.setAmount(amount == null ? 1 : amount);
        players.forEach(player -> PlayerUtil.give(player, item));
        plugin.getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_GIVEN_UNWRAPPER);
    }

}
