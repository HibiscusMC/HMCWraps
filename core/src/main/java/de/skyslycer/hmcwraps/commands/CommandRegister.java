package de.skyslycer.hmcwraps.commands;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.commands.annotation.*;
import de.skyslycer.hmcwraps.commands.exception.CustomExceptionHandler;
import de.skyslycer.hmcwraps.commands.parameter.WrapParameterType;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.Bukkit;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class CommandRegister {

    public static void registerCommands(HMCWrapsPlugin plugin) {
        Lamp<BukkitCommandActor> commandHandler = BukkitLamp.builder(plugin)
                .parameterTypes(types -> types.addParameterType(Wrap.class, new WrapParameterType(plugin)))
                .suggestionProviders(providers -> {
                    providers.addProviderForAnnotation(PhysicalWraps.class, physicalWraps ->
                            executionContext -> plugin.getWrapsLoader().getWraps()
                                    .values().stream().filter(wrap -> wrap.getPhysical() != null).map(Wrap::getUuid).toList());
                    providers.addProviderForAnnotation(LogFiles.class, logFiles ->
                            executionContext -> suggestLogFiles(executionContext.input().peekString()));
                    providers.addProviderForAnnotation(PluginFiles.class, pluginFiles ->
                            executionContext -> suggestPluginFiles(executionContext.input().peekString()));
                    providers.addProviderForAnnotation(JsonFiles.class, jsonFiles ->
                            executionContext -> suggestJsonFiles(executionContext.input().peekString()));
                    providers.addProviderForAnnotation(WrapCategories.class, wrapCategories ->
                            executionContext -> suggestWrapCategories(executionContext.input().peekString(), plugin));
                })
                .permissionFactory(new AnyPermissionFactory())
                .exceptionHandler(new CustomExceptionHandler(plugin))
                .build();
        commandHandler.register(new WrapCommand(plugin), new WrapCreateCommand(plugin), new DebugCommand(plugin));
        if (isTestModeEnabled()) {
            commandHandler.register(new TestCommand(plugin));
        }
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            commandHandler.register(new PermissionCommand(plugin));
        }
    }

    private static List<String> suggestWrapCategories(String current, HMCWraps plugin) {
        return plugin.getWrapsLoader().getTypeWraps().keySet().stream().toList();
    }

    private static List<String> suggestLogFiles(String current) {
        List<String> fileList;
        try (var files = Files.list(Path.of("logs"))) {
            fileList = files.filter(path -> !Files.isDirectory(path)).map(Path::getFileName).map(Path::toString)
                    .filter(name -> name.startsWith(current)).toList();
        } catch (Exception exception) {
            return Collections.emptyList();
        }
        return fileList;
    }

    private static List<String> suggestPluginFiles(String current) {
        return suggestFiles(current, path -> true);
    }

    private static List<String> suggestJsonFiles(String current) {
        return suggestFiles(current, path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json"));
    }

    private static List<String> suggestFiles(String current, Predicate<Path> fileFilter) {
        var path = HMCWraps.PLUGIN_PATH;
        if (current.contains("/")) {
            for (String folder : current.substring(0, current.lastIndexOf("/")).split("/")) {
                if (folder.isEmpty() || folder.equals(".") || folder.equals("..")) {
                    continue;
                }
                path = path.resolve(folder);
            }
        }

        List<String> fileList;
        try (var files = Files.list(path)) {
            var additional = HMCWraps.PLUGIN_PATH.relativize(path);
            var additionalText = additional.toString().isEmpty() ? "" : additional + "/";
            fileList = files
                    .filter(filePath -> Files.isDirectory(filePath) || fileFilter.test(filePath))
                    .map(filePath -> Files.isDirectory(filePath)
                            ? additionalText + filePath.getFileName() + "/"
                            : additionalText + filePath.getFileName())
                    .toList();
        } catch (Exception exception) {
            return Collections.emptyList();
        }
        return fileList.stream().map(string -> string.replace('\\', '/')).toList();
    }

    private static boolean isTestModeEnabled() {
        return Files.exists(Path.of("plugins", "HMCWraps", "testmode"));
    }

}
