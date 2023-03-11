package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.CommandHelp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Command("wraps")
public class WrapCommand {

    public static final String RELOAD_PERMISSION = "hmcwraps.commands.reload";
    public static final String CONVERT_PERMISSION = "hmcwraps.commands.convert";
    public static final String WRAP_PERMISSION = "hmcwraps.commands.wrap";
    public static final String UNWRAP_PERMISSION = "hmcwraps.commands.unwrap";
    public static final String GIVE_WRAPPER_PERMISSION = "hmcwraps.commands.give.wrapper";
    public static final String GIVE_UNWRAPPER_PERMISSION = "hmcwraps.commands.give.unwrapper";
    public static final String PREVIEW_PERMISSION = "hmcwraps.commands.preview";
    public static final String LIST_PERMISSION = "hmcwraps.commands.list";
    public static final String WRAPS_PERMISSION = "hmcwraps.wraps";

    private final Set<String> confirmingPlayers = new HashSet<>();

    private final HMCWrapsPlugin plugin;

    public WrapCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Open the wrap inventory.")
    public void onWraps(Player player) {
        if (plugin.getConfiguration().getPermissions().isInventoryPermission() && !player.hasPermission(WRAPS_PERMISSION)) {
            plugin.getMessageHandler().send(player, Messages.NO_PERMISSION);
            return;
        }
        var item = player.getInventory().getItemInMainHand();
        if (item.getType().isAir()) {
            plugin.getMessageHandler().send(player, Messages.NO_ITEM);
            return;
        }
        if (plugin.getCollectionHelper().getItems(item.getType()).isEmpty()) {
            plugin.getMessageHandler().send(player, Messages.NO_WRAPS);
            return;
        }
        GuiBuilder.open(plugin, player, player.getInventory().getItemInMainHand());
    }

    @Subcommand("reload")
    @CommandPermission(RELOAD_PERMISSION)
    @Description("Reload configuration and messages.")
    public void onReload(CommandSender sender) {
        plugin.unload();
        plugin.load();
        plugin.getMessageHandler().send(sender, Messages.COMMAND_RELOAD);
    }

    @Subcommand("convert")
    @CommandPermission(CONVERT_PERMISSION)
    @Description("Convert ItemSkins files to HMCWraps files.")
    public void onConvert(CommandSender sender, @Optional String confirm) {
        var uuid = "CONSOLE";
        if (sender instanceof Player player) {
            uuid = player.getUniqueId().toString();
        }
        if (confirm == null || !confirm.equalsIgnoreCase("confirm")) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_CONVERT_CONFIRM);
            confirmingPlayers.add(uuid);
        } else {
            if (!confirmingPlayers.contains(uuid)) {
                plugin.getMessageHandler().send(sender, Messages.COMMAND_CONVERT_NO_CONFIRM);
                return;
            }
            var success = plugin.getFileConverter().convertAll();
            if (!success) {
                plugin.getMessageHandler().send(sender, Messages.COMMAND_CONVERT_FAILED);
                return;
            }
            plugin.unload();
            plugin.load();
            plugin.getMessageHandler().send(sender, Messages.COMMAND_CONVERT_SUCCESS);
            confirmingPlayers.remove(uuid);
        }
    }

    @Subcommand("wrap")
    @Description("Wrap the item a player is holding in their main hand.")
    @CommandPermission(WRAP_PERMISSION)
    @AutoComplete("@wraps @players @actions")
    public void onWrap(CommandSender sender, Wrap wrap, @Default("self") Player player, @Optional String actions) {
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType().isAir()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        for (WrappableItem wrappableItem : plugin.getCollectionHelper().getItems(item.getType())) {
            if (wrappableItem.getWraps().containsValue(wrap)) {
                item = plugin.getWrapper().setWrap(wrap, item, false, player, true);
                item = plugin.getWrapper().setOwningPlayer(item, player.getUniqueId());
                player.getInventory().setItemInMainHand(item);
                if (actions == null || !actions.equals("-actions")) {
                    plugin.getActionHandler().pushWrap(wrap, player);
                    plugin.getActionHandler().pushVirtualWrap(wrap, player);
                }
                plugin.getMessageHandler().send(sender, Messages.COMMAND_WRAP_WRAPPED);
                return;
            }
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_ITEM_NOT_FOR_WRAP);
    }

    @Subcommand("unwrap")
    @Description("Unwrap the item a player is holding in their main hand.")
    @CommandPermission(UNWRAP_PERMISSION)
    @AutoComplete("@players @actions")
    public void onUnwrap(CommandSender sender, @Default("self") Player player, @Optional String actions) {
        var item = player.getInventory().getItemInMainHand().clone();
        if (item.getType().isAir()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NEED_ITEM);
            return;
        }
        var wrap = plugin.getWrapper().getWrap(item);
        if (wrap == null) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_ITEM_NOT_WRAPPED);
            return;
        }
        item = plugin.getWrapper().removeWrap(item, player, true);
        player.getInventory().setItemInMainHand(item);
        if (actions == null || !actions.equals("-actions")) {
            plugin.getActionHandler().pushUnwrap(wrap, player);
            plugin.getActionHandler().pushVirtualUnwrap(wrap, player);
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_UNWRAP_UNWRAPPED);
    }

    @Subcommand("preview")
    @Description("Preview a wrap for the specified player.")
    @CommandPermission(PREVIEW_PERMISSION)
    @AutoComplete("@wraps @players @actions")
    public void onPreview(CommandSender sender, Wrap wrap, @Default("self") Player player, @Optional String actions) {
        var material = plugin.getCollectionHelper().getMaterial(wrap);
        if (material == null) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_NO_MATCHING_ITEM);
            return;
        }
        plugin.getPreviewManager().create(player, null, wrap);
        if (actions == null || !actions.equals("-actions")) {
            plugin.getActionHandler().pushPreview(wrap, player);
        }
        plugin.getMessageHandler().send(sender, Messages.COMMAND_PREVIEW_CREATED);
    }

    @Subcommand("give wrapper")
    @Description("Give a wrapper to a player.")
    @CommandPermission(GIVE_WRAPPER_PERMISSION)
    @AutoComplete("@physicalWraps @players *")
    public void onGiveWrap(CommandSender sender, Wrap wrap, @Default("self") Player player, @Range(min = 1, max = 64) @Optional Integer amount) {
        if (wrap.getPhysical() == null) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_INVALID_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
            return;
        }
        var item = wrap.getPhysical().toItem(plugin, player);
        item.setAmount(amount == null ? 1 : amount);
        PlayerUtil.give(player, plugin.getWrapper().setPhysicalWrapper(item, wrap));
        plugin.getMessageHandler().send(sender, Messages.COMMAND_GIVEN_PHYSICAL, Placeholder.parsed("uuid", wrap.getUuid()));
    }

    @Subcommand("give unwrapper")
    @Description("Give an unwrapper to a player.")
    @CommandPermission(GIVE_UNWRAPPER_PERMISSION)
    @AutoComplete("@players *")
    public void onGiveUnwrapper(CommandSender sender, @Default("self") Player player, @Optional @Range(min = 1, max = 64) Integer amount) {
        var item = plugin.getConfiguration().getUnwrapper().toItem(plugin, player);
        item.setAmount(amount == null ? 1 : amount);
        PlayerUtil.give(player, plugin.getWrapper().setPhysicalUnwrapper(item));
        plugin.getMessageHandler().send(sender, Messages.COMMAND_GIVEN_UNWRAPPER);
    }

    @Subcommand("list")
    @Description("Shows all wraps and collections configured.")
    @CommandPermission(LIST_PERMISSION)
    public void onList(CommandSender sender) {
        var handler = plugin.getMessageHandler();
        var set = new ArrayList<Component>();
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_HEADER)));
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_COLLECTIONS)));
        plugin.getWrapsLoader().getCollections().forEach((key, list) -> {
            set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_KEY_FORMAT), Placeholder.parsed("value", key)));
            list.forEach(entry -> set.add(
                    StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_COLLECTIONS_FORMAT), Placeholder.parsed("value", entry))));
        });
        set.add(Component.space());
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_WRAPS)));
        plugin.getWrapsLoader().getWrappableItems().forEach((material, wraps) -> {
            set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_KEY_FORMAT), Placeholder.parsed("value", material)));
            wraps.getWraps().forEach((ignored, wrap) -> {
                var uuid = wrap.getUuid();
                var placeholders = List.of(Placeholder.parsed("value", uuid), Placeholder.parsed("permission", wrap.getPermission() == null ? "None" : wrap.getPermission()),
                        Placeholder.parsed("modelid", String.valueOf(wrap.getModelId())),
                        Placeholder.parsed("player", sender instanceof Player player ? player.getName() : " "),
                        Placeholder.parsed("physical", String.valueOf(wrap.getPhysical() != null)),
                        Placeholder.parsed("preview", String.valueOf(wrap.isPreview())));
                set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_WRAPS_FORMAT), placeholders.toArray(Single[]::new)));
            });
        });
        var component = Component.empty();
        for (Component entry : set) {
            component = component.append(entry).append(Component.newline());
        }
        sender.spigot().sendMessage(BungeeComponentSerializer.get().serialize(component));
    }

    @Subcommand("help")
    @Description("Shows the help page.")
    public void onHelp(CommandSender sender, CommandHelp<String> helpEntries) {
        plugin.getMessageHandler().send(sender, Messages.COMMAND_HELP_HEADER);
        Supplier<Stream<String>> filteredEntries = () -> helpEntries.paginate(1, 100).stream().filter(string -> !string.equals(""));
        if (filteredEntries.get().findAny().isEmpty()) {
            plugin.getMessageHandler().send(sender, Messages.COMMAND_HELP_NO_PERMISSION);
        } else {
            filteredEntries.get().forEach((line) -> StringUtil.send(sender, line));
        }
    }

}
