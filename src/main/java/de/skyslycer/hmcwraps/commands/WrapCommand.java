package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.debug.DebugCreator;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.debug.Debuggable;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver.Single;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.help.CommandHelp;

import java.nio.file.Files;
import java.nio.file.Path;
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
    public static final String DEBUG_PERMISSION = "hmcwraps.debug";

    private final Set<String> confirmingPlayers = new HashSet<>();

    private final HMCWrapsPlugin plugin;

    public WrapCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    @Description("Open the wrap inventory.")
    public void onWraps(Player player) {
        if (plugin.getConfiguration().getPermissions().isInventoryPermission() && !PermissionUtil.hasAnyPermission(player, WRAPS_PERMISSION)) {
            plugin.getMessageHandler().send(player, Messages.NO_PERMISSION);
            return;
        }
        var item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
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
        if (item.getType() == Material.AIR) {
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
        if (item.getType() == Material.AIR) {
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
        plugin.getCollections().forEach((key, list) -> {
            set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_KEY_FORMAT), Placeholder.parsed("value", key)));
            list.forEach(entry -> set.add(
                    StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_COLLECTIONS_FORMAT), Placeholder.parsed("value", entry))));
        });
        set.add(Component.space());
        set.add(StringUtil.parseComponent(sender, handler.get(Messages.COMMAND_LIST_WRAPS)));
        plugin.getWrappableItems().forEach((material, wraps) -> {
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

    @Subcommand("debug info")
    @Description("Debugs plugin and server information.")
    @AutoComplete("@upload")
    @CommandPermission(DEBUG_PERMISSION)
    public void onDebugInformation(CommandSender sender, @Optional String upload) {
        uploadAndSend(sender, DebugCreator.createDebugInformation(plugin), upload != null && upload.equalsIgnoreCase("-upload"));
    }

    @Subcommand("debug config")
    @Description("Debugs plugin configuration.")
    @AutoComplete("@upload")
    @CommandPermission(DEBUG_PERMISSION)
    public void onDebugConfig(CommandSender sender, @Optional String upload) {
        uploadAndSend(sender, DebugCreator.createDebugConfig(plugin), upload != null && upload.equalsIgnoreCase("-upload"));
    }

    @Subcommand("debug wraps")
    @Description("Debugs wraps and collections.")
    @AutoComplete("@upload")
    @CommandPermission(DEBUG_PERMISSION)
    public void onDebugWraps(CommandSender sender, @Optional String upload) {
        uploadAndSend(sender, DebugCreator.createDebugWraps(plugin), upload != null && upload.equalsIgnoreCase("-upload"));
    }

    @Subcommand("debug wrap")
    @Description("Debugs one wrap.")
    @AutoComplete("@wraps @upload")
    @CommandPermission(DEBUG_PERMISSION)
    public void onDebugWrap(CommandSender sender, Wrap wrap, @Optional String upload) {
        uploadAndSend(sender, DebugCreator.createDebugWrap(plugin, wrap), upload != null && upload.equalsIgnoreCase("-upload"));
    }

    @Subcommand("debug player")
    @Description("Debugs a player.")
    @AutoComplete("@players @upload")
    @CommandPermission(DEBUG_PERMISSION)
    public void onDebugPlayer(CommandSender sender, Player player, @Optional String upload) {
        uploadAndSend(sender, DebugCreator.createDebugPlayer(plugin, player), upload != null && upload.equalsIgnoreCase("-upload"));
    }

    @Subcommand("debug log")
    @Description("Uploads a server log.")
    @AutoComplete("@log")
    @CommandPermission(DEBUG_PERMISSION)
    public void onDebugLog(CommandSender sender, String log) {
        var path = Path.of("logs").resolve(log);
        if (!checkFile(sender, path)) {
            return;
        }
        handleLink(sender, DebugCreator.uploadLog(path).orElse(null), "log");
    }

    @Subcommand("debug upload")
    @Description("Uploads a configuration file.")
    @AutoComplete("@file")
    @CommandPermission(DEBUG_PERMISSION)
    public void onDebugUpload(CommandSender sender, String file) {
        var path = HMCWrapsPlugin.PLUGIN_PATH;
        if (file.contains("/")) {
            for (String folder : file.substring(0, file.lastIndexOf("/")).split("/")) {
                path = path.resolve(folder);
            }
            path = path.resolve(file.substring(file.lastIndexOf("/") + 1));
        } else {
            path = path.resolve(file);
        }
        if (!checkFile(sender, path)) {
            return;
        }
        try {
            var contents = Files.readString(path);
            var type = "plain";
            if (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml")) {
                type = "yaml";
            }
            handleLink(sender, DebugCreator.upload(contents, type).orElse(null), path.getFileName().toString());
        } catch (Exception exception) {
            StringUtil.sendComponent(sender, Component.text("Failed to upload file! Please check the console.").color(NamedTextColor.RED));
            plugin.logSevere("Failed to upload file " + path + "!");
            exception.printStackTrace();
        }
    }

    private void uploadAndSend(CommandSender sender, Debuggable debuggable, boolean upload) {
        plugin.getLogger().info("Debug information (" + debuggable.getClass().getSimpleName() + "): \n" + DebugCreator.debugToJson(debuggable));
        StringUtil.sendComponent(sender, Component.text("Debug information (" + debuggable.getClass().getSimpleName() + ") printed to console.").color(NamedTextColor.GREEN));
        if (upload) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
                var link = DebugCreator.upload(DebugCreator.debugToJson(debuggable), "json");
                handleLink(sender, link.orElse(null), debuggable.getClass().getSimpleName());
            }, 0L);
        }
    }

    private void handleLink(CommandSender sender, String link, String type) {
        if (link != null && !link.equals("Too large")) {
            StringUtil.sendComponent(sender, Component.text("Successfully uploaded (" + type + "): ").color(NamedTextColor.GRAY)
                    .append(Component.text(link).clickEvent(ClickEvent.openUrl(link))
                            .hoverEvent(HoverEvent.showText(Component.text("Click to open!").color(NamedTextColor.AQUA))).color(NamedTextColor.BLUE)));
        } else {
            StringUtil.sendComponent(sender, Component.text("Failed to upload debug information or file! Please check the console.").color(NamedTextColor.RED));
        }
    }

    private boolean checkFile(CommandSender sender, Path path) {
        if (Files.notExists(path)) {
            StringUtil.sendComponent(sender, Component.text("This file does not exist!").color(NamedTextColor.RED));
            return false;
        }
        if (Files.isDirectory(path)) {
            StringUtil.sendComponent(sender, Component.text("This file is a directory!").color(NamedTextColor.RED));
            return false;
        }
        return true;
    }

}
