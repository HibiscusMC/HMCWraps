package de.skyslycer.hmcwraps;

import com.github.retrooper.packetevents.PacketEvents;
import com.tchristofferson.configupdater.ConfigUpdater;
import de.skyslycer.hmcwraps.actions.ActionHandler;
import de.skyslycer.hmcwraps.actions.IActionHandler;
import de.skyslycer.hmcwraps.actions.register.DefaultActionRegister;
import de.skyslycer.hmcwraps.commands.WrapCommand;
import de.skyslycer.hmcwraps.converter.FileConverter;
import de.skyslycer.hmcwraps.itemhook.ItemHook;
import de.skyslycer.hmcwraps.itemhook.ItemsAdderItemHook;
import de.skyslycer.hmcwraps.itemhook.OraxenItemHook;
import de.skyslycer.hmcwraps.listener.*;
import de.skyslycer.hmcwraps.messages.IMessageHandler;
import de.skyslycer.hmcwraps.messages.MessageHandler;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.placeholderapi.HMCWrapsPlaceholders;
import de.skyslycer.hmcwraps.pool.MessagePool;
import de.skyslycer.hmcwraps.pool.ObjectPool;
import de.skyslycer.hmcwraps.preview.IPreviewManager;
import de.skyslycer.hmcwraps.preview.PreviewManager;
import de.skyslycer.hmcwraps.serialization.*;
import de.skyslycer.hmcwraps.serialization.files.CollectionFile;
import de.skyslycer.hmcwraps.serialization.files.WrapFile;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.storage.FavoriteWrapStorage;
import de.skyslycer.hmcwraps.storage.PlayerFilterStorage;
import de.skyslycer.hmcwraps.storage.Storage;
import de.skyslycer.hmcwraps.updater.ContinuousUpdateChecker;
import de.skyslycer.hmcwraps.wrap.CollectionHelper;
import de.skyslycer.hmcwraps.wrap.ICollectionHelper;
import de.skyslycer.hmcwraps.wrap.IWrapper;
import de.skyslycer.hmcwraps.wrap.Wrapper;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.autocomplete.SuggestionProviderFactory;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.MissingArgumentException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class HMCWraps extends JavaPlugin implements IHMCWraps {

    private static final YamlConfigurationLoader LOADER = YamlConfigurationLoader.builder()
            .defaultOptions(ConfigurationOptions.defaults().implicitInitialization(false))
            .path(CONFIG_PATH)
            .build();

    private final ObjectPool<UUID, Component> messagePool = new MessagePool();
    private final Set<ItemHook> hooks = new HashSet<>();
    private final Map<String, IWrap> wraps = new LinkedHashMap<>();
    private final Map<String, List<String>> collections = new HashMap<>();
    private final Map<String, IWrappableItem> wrappableItems = new HashMap<>();
    private final Set<IWrapFile> wrapFiles = new HashSet<>();
    private final Set<ICollectionFile> collectionFiles = new HashSet<>();
    private final Set<String> loadedHooks = new HashSet<>();
    private final IWrapper wrapper = new Wrapper(this);
    private final IPreviewManager previewManager = new PreviewManager(this);
    private final ICollectionHelper collectionHelper = new CollectionHelper(this);
    private final IActionHandler actionHandler = new ActionHandler();
    private final FileConverter fileConverter = new FileConverter(this);
    private final Storage<Player, Boolean> filterStorage = new PlayerFilterStorage(this);
    private final Storage<Player, List<IWrap>> favoriteWrapStorage = new FavoriteWrapStorage(this);
    private final ContinuousUpdateChecker updateChecker = new ContinuousUpdateChecker(this);
    private IConfig config;
    private IMessageHandler messageHandler;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getSettings().checkForUpdates(false);
    }

    @Override
    public void onEnable() {
        checkDependency("PlaceholderAPI", false);
        if (checkDependency("ItemsAdder", false)) {
            hooks.add(new ItemsAdderItemHook());
        }
        if (checkDependency("Oraxen", false)) {
            hooks.add(new OraxenItemHook());
        }

        if (!load()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerShiftListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPickupListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryOpenListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        PacketEvents.getAPI().init();

        registerCommands();

        new DefaultActionRegister(this).register();
        new PluginMetrics(this).init();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new HMCWrapsPlaceholders(this).register();
        }
    }

    @Override
    public void onDisable() {
        unload();
        hooks.clear();
        PacketEvents.getAPI().terminate();
    }

    @Override
    public boolean load() {
        if (Files.notExists(PLUGIN_PATH)) {
            try {
                Files.createDirectory(PLUGIN_PATH);
            } catch (IOException exception) {
                logSevere("Could not create the folder (please report this to the developers)! The plugin will shut down now.");
                exception.printStackTrace();
                return false;
            }
        }
        if (!loadConfig()) {
            return false;
        }
        if (!loadMessages()) {
            return false;
        }
        getPreviewManager().removeAll(true);
        getUpdateChecker().check();
        return true;
    }

    @Override
    public void unload() {
        wraps.clear();
        wrappableItems.clear();
        wrapFiles.clear();
        collectionFiles.clear();
    }

    private void registerCommands() {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);

        commandHandler.registerValueResolver(Wrap.class, context -> {
            var wrap = (Wrap) getWraps().get(context.pop());
            if (wrap == null) {
                getMessageHandler().send(context.actor().as(BukkitActor.class).getAsPlayer(), Messages.COMMAND_INVALID_WRAP,
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
                (args, sender, command) -> getWraps().values().stream().filter(wrap -> wrap.getPhysical() != null).map(IWrap::getUuid).toList());
        commandHandler.getAutoCompleter()
                .registerSuggestion("wraps", ((args, sender, command) -> getWraps().values().stream().map(IWrap::getUuid).toList()));
        commandHandler.getAutoCompleter().registerSuggestion("upload", "-upload");
        commandHandler.getAutoCompleter().registerSuggestion("actions", "-actions");
        commandHandler.getAutoCompleter().registerSuggestion("file", (args, sender, command) -> {
            var current = args.get(3);
            var path = PLUGIN_PATH;
            if (current.contains("/")) {
                for (String folder : current.substring(0, current.lastIndexOf("/")).split("/")) {
                    path = path.resolve(folder);
                }
            }
            List<String> fileList;
            try (var files = Files.list(path)) {
                var additional = PLUGIN_PATH.relativize(path);
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
                (actor, context) -> getMessageHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_PLAYER_ONLY));
        commandHandler.registerExceptionHandler(MissingArgumentException.class,
                (actor, context) -> getMessageHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_MISSING_ARGUMENT,
                        Placeholder.parsed("argument", context.getParameter().getName())));
        commandHandler.disableStackTraceSanitizing();
        commandHandler.setHelpWriter(
                (command, actor) -> command.getPermission().canExecute(actor) ? getMessageHandler().get(Messages.COMMAND_HELP_FORMAT)
                        .replace("<command>", command.getPath().toRealString())
                        .replace("<usage>", command.getUsage()).replace("<description>", command.getDescription()) : "");
        commandHandler.register(new WrapCommand(this));
        commandHandler.registerBrigadier();
    }

    private boolean loadMessages() {
        try {
            if (Files.notExists(MESSAGES_PATH)) {
                Files.copy(this.getClassLoader().getResourceAsStream("messages.properties"), MESSAGES_PATH);
            }
        } catch (IOException exception) {
            logSevere(
                    "Could not copy the configuration (please report this to the developers)! The plugin will shut down now.");
            exception.printStackTrace();
            return false;
        }
        messageHandler = new MessageHandler(this);
        messageHandler.update(MESSAGES_PATH);
        return messageHandler.load(MESSAGES_PATH);
    }

    private boolean loadConfig() {
        try {
            if (Files.notExists(WRAP_FILES_PATH)) {
                Files.createDirectory(WRAP_FILES_PATH);
                Files.copy(getResource("silver_wraps.yml"), WRAP_FILES_PATH.resolve("silver_wraps.yml"));
            }
            if (Files.notExists(COLLECTION_FILES_PATH)) {
                Files.createDirectory(COLLECTION_FILES_PATH);
                Files.copy(getResource("some_collections.yml"), COLLECTION_FILES_PATH.resolve("some_collections.yml"));
            }
            if (Files.notExists(CONVERT_PATH)) {
                Files.createDirectory(CONVERT_PATH);
            }
            if (Files.notExists(CONFIG_PATH)) {
                Files.copy(getResource("config.yml"), CONFIG_PATH);
            }
            ConfigUpdater.update(this, "config.yml", CONFIG_PATH.toFile(), "items", "inventory.items", "collections",
                    "preservation.model-id.defaults", "preservation.color.defaults", "preservation.name.defaults", "unwrapper", "inventory.actions");
            config = LOADER.load().get(Config.class);
            loadWrapFiles();
            loadCollectionFiles();
            combineFiles();
        } catch (IOException exception) {
            logSevere(
                    "Could not load the configuration (please report this to the developers)! The plugin will shut down now.");
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    private void combineFiles() {
        collections.putAll(getConfiguration().getCollections());
        collectionFiles.stream().filter(IToggleable::isEnabled).forEach(collectionFile -> collections.putAll(collectionFile.getCollections()));

        wrappableItems.putAll(getConfiguration().getItems());
        wrapFiles.forEach(it -> it.getItems().forEach((type, wrappableItem) -> {
            if (wrappableItems.containsKey(type)) {
                var current = (WrappableItem) wrappableItems.get(type);
                var toAdd = (WrappableItem) wrappableItem;
                toAdd.getWrapsPrivate().values().forEach(wrap -> current.putWrap(current.getWraps().size() + 1 + "", wrap));
                wrappableItems.put(type, current);
            } else {
                wrappableItems.put(type, wrappableItem);
            }
        }));
        wrappableItems.values().forEach(wrappableItem -> wrappableItem.getWraps().values().forEach(wrap -> wraps.put(wrap.getUuid(), wrap)));

        wraps.remove("-");
    }

    private void loadWrapFiles() {
        try (Stream<Path> paths = Files.find(WRAP_FILES_PATH, 10,
                ((path, attributes) -> attributes.isRegularFile() && (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))))) {
            paths.forEach(path -> {
                try {
                    var wrapFile = YamlConfigurationLoader.builder()
                            .defaultOptions(ConfigurationOptions.defaults().implicitInitialization(false))
                            .path(path)
                            .build().load().get(WrapFile.class);
                    if (wrapFile != null && wrapFile.isEnabled()) {
                        wrapFiles.add(wrapFile);
                    }
                } catch (ConfigurateException exception) {
                    logSevere(
                            "Could not load the wrap file " + path.getFileName().toString() + " (please report this to the developers)!");
                    exception.printStackTrace();
                }
            });
        } catch (IOException exception) {
            logSevere(
                    "Could not find the wrap files (please report this to the developers)!");
            exception.printStackTrace();
        }
    }

    private void loadCollectionFiles() {
        try (Stream<Path> paths = Files.find(COLLECTION_FILES_PATH, 10,
                ((path, attributes) -> attributes.isRegularFile() && (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))))) {
            paths.forEach(path -> {
                try {
                    var collectionFile = YamlConfigurationLoader.builder()
                            .defaultOptions(ConfigurationOptions.defaults().implicitInitialization(false))
                            .path(path)
                            .build().load().get(CollectionFile.class);
                    if (collectionFile != null && collectionFile.isEnabled()) {
                        collectionFiles.add(collectionFile);
                    }
                } catch (ConfigurateException exception) {
                    logSevere(
                            "Could not load the collection file " + path.getFileName().toString() + " (please report this to the developers)!");
                    exception.printStackTrace();
                }
            });
        } catch (IOException exception) {
            logSevere(
                    "Could not find the wrap files (please report this to the developers)!");
            exception.printStackTrace();
        }
    }

    private boolean checkDependency(String name, boolean needed) {
        if (Bukkit.getPluginManager().getPlugin(name) == null) {
            if (needed) {
                logSevere("""
                        The plugin '""" + name + """
                        ' is a required dependency but was not found on this server! Please restart the server after you have added the missing plugin!
                        This plugin will shut down now.""");
            }
            return false;
        }
        if (!loadedHooks.contains(name)) {
            getLogger().info("Plugin '" + name + "' found. Initializing hook.");
            loadedHooks.add(name);
        }
        return true;
    }

    @Override
    public ItemStack getItemFromHook(String id) {
        var possible = hooks.stream().filter(it -> id.startsWith(it.getPrefix())).findFirst();
        if (possible.isEmpty()) {
            return ItemHook.defaultHook.get(id);
        } else {
            return possible.get().get(id.replace(possible.get().getPrefix(), ""));
        }
    }

    @Override
    public int getModelIdFromHook(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ignored) {
            var possible = hooks.stream().filter(it -> id.startsWith(it.getPrefix())).findFirst();
            return possible.map(itemHook -> itemHook.getModelId(id.replace(possible.get().getPrefix(), ""))).orElse(-1);
        }
    }

    @Override
    public void logSevere(String message) {
        getLogger().severe(
                "\n=============================\n" +
                        message + "\n" +
                        "============================="
        );
    }

    @Override
    public int getWrapAmount() {
        int count = 0;
        for (IWrappableItem item : getWrappableItems().values()) {
            count += item.getWraps().size();
        }
        return count;
    }

    @Override
    @NotNull
    public IConfig getConfiguration() {
        return config;
    }

    @Override
    @NotNull
    public IMessageHandler getMessageHandler() {
        return messageHandler;
    }

    @Override
    @NotNull
    public Map<String, IWrap> getWraps() {
        return wraps;
    }

    @Override
    @NotNull
    public Set<IWrapFile> getWrapFiles() {
        return wrapFiles;
    }

    @Override
    @NotNull
    public Map<String, IWrappableItem> getWrappableItems() {
        return wrappableItems;
    }

    @Override
    @NotNull
    public IWrapper getWrapper() {
        return wrapper;
    }

    @Override
    @NotNull
    public IPreviewManager getPreviewManager() {
        return previewManager;
    }

    @Override
    @NotNull
    public ICollectionHelper getCollectionHelper() {
        return collectionHelper;
    }

    @Override
    @NotNull
    public IActionHandler getActionHandler() {
        return actionHandler;
    }

    @Override
    @NotNull
    public Map<String, List<String>> getCollections() {
        return collections;
    }

    @Override
    @NotNull
    public Set<ICollectionFile> getCollectionFiles() {
        return collectionFiles;
    }

    @Override
    public ObjectPool<UUID, Component> getMessagePool() {
        return messagePool;
    }

    @Override
    public Storage<Player, Boolean> getFilterStorage() {
        return filterStorage;
    }

    @Override
    public Storage<Player, List<IWrap>> getFavoriteWrapStorage() {
        return favoriteWrapStorage;
    }

    public FileConverter getFileConverter() {
        return fileConverter;
    }

    public ContinuousUpdateChecker getUpdateChecker() {
        return updateChecker;
    }

}
