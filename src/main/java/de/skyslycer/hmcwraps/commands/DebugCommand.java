package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.commands.annotations.NoHelp;
import de.skyslycer.hmcwraps.debug.DebugCreator;
import de.skyslycer.hmcwraps.serialization.debug.Debuggable;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.StringUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.nio.file.Files;
import java.nio.file.Path;

@NoHelp
@Command("wraps")
public class DebugCommand {

    public static final String DEBUG_PERMISSION = "hmcwraps.debug";

    private final HMCWrapsPlugin plugin;

    public DebugCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
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
    public void onDebugLog(CommandSender sender, @Default("latest.log") @Optional String log) {
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
            plugin.logSevere("Failed to upload file " + path + "!", exception);
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
