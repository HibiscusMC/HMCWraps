package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.autocomplete.SuggestionProviderFactory;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.MissingArgumentException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class CommandRegister {

    public static void registerCommands(HMCWrapsPlugin plugin) {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(plugin);

        commandHandler.registerValueResolver(Wrap.class, context -> {
            var wrap = plugin.getWrapsLoader().getWraps().get(context.pop());
            if (wrap == null) {
                plugin.getMessageHandler().send(context.actor().as(BukkitActor.class).getAsPlayer(), Messages.COMMAND_INVALID_WRAP,
                        Placeholder.parsed("uuid", context.pop()));
                throw new IllegalArgumentException();
            }
            return wrap;
        });
        commandHandler.getAutoCompleter().registerSuggestionFactory(0,
                SuggestionProviderFactory.forType(Player.class, SuggestionProvider.map(Bukkit::getOnlinePlayers, Player::getName)));
        commandHandler.getAutoCompleter().registerParameterSuggestions(Integer.class, SuggestionProvider.of(IntStream.range(1, 65).boxed().map(
                Object::toString).sorted().toList()));
        commandHandler.getAutoCompleter().registerSuggestion("physicalWraps",
                (args, sender, command) -> plugin.getWrapsLoader().getWraps().values().stream().filter(wrap -> wrap.getPhysical() != null).map(Wrap::getUuid).toList());
        commandHandler.getAutoCompleter()
                .registerSuggestion("wraps", ((args, sender, command) -> plugin.getWrapsLoader().getWraps().values().stream().map(Wrap::getUuid).toList()));
        commandHandler.getAutoCompleter().registerSuggestion("upload", "-upload");
        commandHandler.getAutoCompleter().registerSuggestion("actions", "-actions");
        commandHandler.getAutoCompleter().registerSuggestion("file", (args, sender, command) -> {
            var current = args.get(3);
            var path = HMCWraps.PLUGIN_PATH;
            if (current.contains("/")) {
                for (String folder : current.substring(0, current.lastIndexOf("/")).split("/")) {
                    path = path.resolve(folder);
                }
            }
            List<String> fileList;
            try (var files = Files.list(path)) {
                var additional = HMCWraps.PLUGIN_PATH.relativize(path);
                var additionalText = additional.toString().equals("") ? "" : additional + "/";
                fileList = files.map(filePath -> Files.isDirectory(filePath) ? additionalText + filePath.getFileName() + "/" : additionalText + filePath.getFileName()).toList();
            } catch (Exception exception) {
                return Collections.emptyList();
            }
            return fileList.stream().map(string -> string.replace('\\', '/')).toList();
        });
        commandHandler.getAutoCompleter().registerSuggestion("log", (args, sender, command) -> {
            var current = args.get(3);
            List<String> fileList;
            try (var files = Files.list(Path.of("logs"))) {
                fileList = files.filter(path -> !Files.isDirectory(path)).map(Path::getFileName).map(Path::toString)
                        .filter(name -> current.equals("") || name.startsWith(current)).toList();
            } catch (Exception exception) {
                return Collections.emptyList();
            }
            return fileList;
        });
        commandHandler.registerExceptionHandler(SenderNotPlayerException.class,
                (actor, context) -> plugin.getMessageHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_PLAYER_ONLY));
        commandHandler.registerExceptionHandler(MissingArgumentException.class,
                (actor, context) -> plugin.getMessageHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_MISSING_ARGUMENT,
                        Placeholder.parsed("argument", context.getParameter().getName())));
        commandHandler.disableStackTraceSanitizing();
        commandHandler.setHelpWriter(
                (command, actor) -> command.getPermission().canExecute(actor) ? plugin.getMessageHandler().get(Messages.COMMAND_HELP_FORMAT)
                        .replace("<command>", command.getPath().toRealString())
                        .replace("<usage>", command.getUsage()).replace("<description>", command.getDescription()) : "");
        commandHandler.register(new WrapCommand(plugin), new DebugCommand(plugin));
        commandHandler.registerBrigadier();
    }

}
