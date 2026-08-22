package de.skyslycer.hmcwraps.commands;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.commands.annotation.JsonFiles;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.node.NodeEqualityPredicate;
import net.luckperms.api.node.matcher.NodeMatcher;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.util.Tristate;
import org.bukkit.command.CommandSender;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PermissionCommand {

    private static final String PERMISSION_TRANSFER_PERMISSION = "hmcwraps.commands.permission";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final DateTimeFormatter FILE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")
            .withZone(ZoneId.systemDefault());

    private final HMCWrapsPlugin plugin;

    public PermissionCommand(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("wraps permission export")
    @CommandPermission(PERMISSION_TRANSFER_PERMISSION)
    @Description("Export all wrap permissions from LuckPerms to import on another HMCWraps instance.")
    public void onPermissionExport(CommandSender sender) {
        plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_EXPORT_RUNNING);
        var users = new HashMap<UUID, Set<String>>();
        var wrapPermissions = configuredPermissions();
        var searches = wrapPermissions.stream()
                .map(permission -> {
                    var matcher = NodeMatcher.key(PermissionNode.builder(permission).build());
                    return LuckPermsProvider.get().getUserManager().searchAll(matcher)
                            .thenAccept(matches -> matches.forEach((uuid, nodes) -> {
                        if (nodes.stream().anyMatch(node -> node.getValue() && !node.hasExpired() && !node.hasExpiry() && node.getContexts().isEmpty())) {
                            synchronized (users) {
                                users.computeIfAbsent(uuid, ignored -> new LinkedHashSet<>()).add(permission);
                            }
                        }
                    }));
                }).toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(searches)
                .thenRunAsync(() -> {
                    var file = new ExportFile(users, null);
                    var path = serialize(file, null);
                    plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_EXPORT_FINISHED,
                            Placeholder.unparsed("file", path.getFileName().toString()),
                            Placeholder.unparsed("wrap_permissions", String.valueOf(wrapPermissions.size())),
                            Placeholder.unparsed("players", String.valueOf(users.size())),
                            Placeholder.unparsed("permissions", String.valueOf(users.values().stream().mapToInt(Set::size).sum()))
                    );
                }).exceptionally(e -> {
                    plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_EXPORT_FAILED);
                    plugin.logSevere("Error exporting permissions to transfer file!", e);
                    return null;
                });
    }

    @Command("wraps permission import")
    @CommandPermission(PERMISSION_TRANSFER_PERMISSION)
    @Description("Import exported wrap permissions back to LuckPerms.")
    public void onPermissionImport(CommandSender sender, @JsonFiles String file) {
        plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_IMPORT_RUNNING);
        deserialize(sender, file, true)
                .thenCompose(exportFile -> {
                    if (exportFile == null) return CompletableFuture.completedFuture(null);
                    var applied = new ConcurrentHashMap<UUID, Set<String>>();
                    var operations = exportFile.permissions.entrySet().stream()
                            .map(entry -> importPermissions(entry.getKey(), entry.getValue(), applied))
                            .toArray(CompletableFuture[]::new);

                    return CompletableFuture.allOf(operations)
                            .thenRunAsync(() -> {
                                exportFile.applied = applied;
                                serialize(exportFile, file);
                                var unique = exportFile.permissions.values().stream().flatMap(Set::stream).distinct().count();
                                var assigned = applied.values().stream().mapToInt(Set::size).sum();
                                var skipped = exportFile.permissions.values().stream().mapToInt(Set::size).sum() - assigned;
                                plugin.getMessageHandler().send(
                                        sender,
                                        Messages.COMMAND_PERMISSION_IMPORT_FINISHED,
                                        Placeholder.unparsed("file", file),
                                        Placeholder.unparsed("unique_permissions", String.valueOf(unique)),
                                        Placeholder.unparsed("players", String.valueOf(exportFile.permissions.size())),
                                        Placeholder.unparsed("applied", String.valueOf(assigned)),
                                        Placeholder.unparsed("skipped", String.valueOf(skipped))
                                );
                            });
                })
                .exceptionally(e -> {
                    plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_IMPORT_FAILED);
                    plugin.logSevere("Error importing permissions from transfer file!", e);
                    return null;
                });
    }

    @Command("wraps permission revert")
    @CommandPermission(PERMISSION_TRANSFER_PERMISSION)
    @Description("Revert the import of permissions using the original export file.")
    public void onPermissionRevert(CommandSender sender, @JsonFiles String file) {
        plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_REVERT_RUNNING);
        deserialize(sender, file, false)
                .thenCompose(exportFile -> {
                    if (exportFile == null) return CompletableFuture.completedFuture(null);
                    var operations = exportFile.applied.entrySet().stream()
                            .map(entry -> LuckPermsProvider.get().getUserManager().loadUser(entry.getKey())
                                    .thenCompose(user -> {
                                        entry.getValue().forEach(permission -> user.data().remove(PermissionNode.builder(permission).build()));
                                        return LuckPermsProvider.get().getUserManager().saveUser(user);
                                    }))
                            .toArray(CompletableFuture[]::new);

                    return CompletableFuture.allOf(operations)
                            .thenRunAsync(() -> {
                                var assigned = exportFile.applied.values().stream().mapToInt(Set::size).sum();
                                exportFile.applied = null;
                                serialize(exportFile, file);
                                plugin.getMessageHandler().send(
                                        sender,
                                        Messages.COMMAND_PERMISSION_REVERT_FINISHED,
                                        Placeholder.unparsed("file", file),
                                        Placeholder.unparsed("permissions", String.valueOf(assigned)),
                                        Placeholder.unparsed("players", String.valueOf(exportFile.permissions.size()))
                                );
                            });
                })
                .exceptionally(e -> {
                    plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_REVERT_FAILED);
                    plugin.logSevere("Error reverting permissions from transfer file!", e);
                    return null;
                });
    }

    private CompletableFuture<Void> importPermissions(UUID uuid, Set<String> permissions, Map<UUID, Set<String>> applied) {
        var userManager = LuckPermsProvider.get().getUserManager();
        return userManager.loadUser(uuid)
                .thenCompose(user -> {
                    var additions = permissions.stream().filter(permission ->
                                    user.data().contains(PermissionNode.builder(permission).build(), NodeEqualityPredicate.EXACT) != Tristate.TRUE
                                            && !user.getCachedData().getPermissionData().checkPermission(permission).asBoolean())
                            .collect(Collectors.toSet());
                    additions.forEach(permission -> user.data().add(PermissionNode.builder(permission).build()));
                    if (!additions.isEmpty()) applied.put(uuid, additions);
                    return userManager.saveUser(user);
                });
    }

    private List<String> configuredPermissions() {
        return plugin.getWrapsLoader().getWraps().values().stream()
                .map(Wrap::getPermission)
                .filter(permission -> permission != null && !permission.isBlank())
                .map(permission -> permission.trim().toLowerCase(Locale.ROOT))
                .distinct()
                .sorted()
                .toList();
    }

    private Path nextExportPath() {
        var base = HMCWraps.PLUGIN_PATH.toAbsolutePath();
        var prefix = "wrap-permissions-" + FILE_TIME.format(Instant.now());
        var path = base.resolve(prefix + ".json");
        for (var suffix = 2; Files.exists(path); suffix++) {
            path = base.resolve(prefix + '-' + suffix + ".json");
        }
        return path;
    }

    private CompletableFuture<ExportFile> deserialize(CommandSender sender, String file, boolean importing) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var base = HMCWraps.PLUGIN_PATH.toAbsolutePath().normalize().toRealPath();
                var path = base.resolve(file).normalize();
                if (!path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".json") || !Files.isRegularFile(path)) {
                    plugin.getMessageHandler().send(sender, Messages.NO_FILE);
                    return null;
                }
                path = path.toRealPath();
                if (!path.startsWith(base)) {
                    plugin.getMessageHandler().send(sender, Messages.BAD_FOLDER);
                    return null;
                }
                var deserialized = GSON.fromJson(Files.readString(path, StandardCharsets.UTF_8), ExportFile.class);
                if (importing && deserialized.applied != null) {
                    plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_IMPORT_ALREADY_IMPORTED);
                    return null;
                }
                if (!importing && deserialized.applied == null) {
                    plugin.getMessageHandler().send(sender, Messages.COMMAND_PERMISSION_REVERT_ALREADY_REVERTED);
                    return null;
                }
                return deserialized;
            } catch (IOException exception) {
                plugin.getMessageHandler().send(sender, importing ? Messages.COMMAND_PERMISSION_IMPORT_FAILED : Messages.COMMAND_PERMISSION_REVERT_FAILED);
                plugin.logSevere("An error occurred while resolving the path for permission file import or revert.", exception);
                return null;
            }
        });
    }

    private Path serialize(ExportFile exportFile, String file) {
        try {
            var path = nextExportPath();
            if (file != null) {
                var base = HMCWraps.PLUGIN_PATH.toAbsolutePath().normalize().toRealPath();
                path = base.resolve(file).normalize().toRealPath();
            }
            Files.writeString(path, GSON.toJson(exportFile), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            return path;
        } catch (IOException exception) {
            throw new CompletionException(exception);
        }
    }

    static class ExportFile {

        private Map<UUID, Set<String>> permissions;
        private Map<UUID, Set<String>> applied;

        public ExportFile(Map<UUID, Set<String>> permissions, Map<UUID, Set<String>> applied) {
            this.permissions = permissions;
            this.applied = applied;
        }

    }

}
