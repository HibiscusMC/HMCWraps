package de.skyslycer.hmcwraps;

import com.bgsoftware.common.config.CommentedConfiguration;
import com.github.retrooper.packetevents.PacketEvents;
import de.skyslycer.hmcwraps.actions.ActionHandler;
import de.skyslycer.hmcwraps.actions.register.DefaultActionRegister;
import de.skyslycer.hmcwraps.commands.CommandRegister;
import de.skyslycer.hmcwraps.converter.FileConverter;
import de.skyslycer.hmcwraps.itemhook.*;
import de.skyslycer.hmcwraps.listener.*;
import de.skyslycer.hmcwraps.messages.MessageHandler;
import de.skyslycer.hmcwraps.messages.MessageHandlerImpl;
import de.skyslycer.hmcwraps.metrics.PluginMetrics;
import de.skyslycer.hmcwraps.nbtapi.logger.NoInfoLogger;
import de.skyslycer.hmcwraps.placeholderapi.HMCWrapsPlaceholders;
import de.skyslycer.hmcwraps.pool.MessagePool;
import de.skyslycer.hmcwraps.pool.ObjectPool;
import de.skyslycer.hmcwraps.preview.PreviewManager;
import de.skyslycer.hmcwraps.serialization.Config;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.storage.FavoriteWrapStorage;
import de.skyslycer.hmcwraps.storage.PlayerFilterStorage;
import de.skyslycer.hmcwraps.storage.Storage;
import de.skyslycer.hmcwraps.transformation.ConfigFileTransformations;
import de.skyslycer.hmcwraps.updater.ContinuousUpdateChecker;
import de.skyslycer.hmcwraps.wrap.*;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import de.tr7zw.changeme.nbtapi.utils.VersionChecker;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class HMCWrapsPlugin extends JavaPlugin implements HMCWraps {

    private static final YamlConfigurationLoader LOADER = YamlConfigurationLoader.builder()
            .defaultOptions(ConfigurationOptions.defaults().implicitInitialization(false))
            .path(CONFIG_PATH)
            .build();

    private final ObjectPool<UUID, Component> messagePool = new MessagePool();
    private final Set<ItemHook> hooks = new HashSet<>();
    private final Set<String> loadedHooks = new HashSet<>();
    private final Wrapper wrapper = new WrapperImpl(this);
    private final PreviewManager previewManager = new PreviewManager(this);
    private final CollectionHelper collectionHelper = new CollectionHelperImpl(this);
    private final ActionHandler actionHandler = new ActionHandler();
    private final FileConverter fileConverter = new FileConverter(this);
    private final Storage<Player, Boolean> filterStorage = new PlayerFilterStorage(this);
    private final Storage<Player, List<Wrap>> favoriteWrapStorage = new FavoriteWrapStorage(this);
    private final ContinuousUpdateChecker updateChecker = new ContinuousUpdateChecker(this);
    private final WrapsLoader wrapsLoader = new WrapsLoaderImpl(this);
    private HookAccessor hookAccessor;
    private Config config;
    private MessageHandler messageHandler;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getSettings().checkForUpdates(false);
        MinecraftVersion.replaceLogger(new NoInfoLogger("HMCWraps-NBT", null));
        VersionChecker.hideOk = true;
        new NBTContainer();
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
        if (checkDependency("Crucible", false)) {
            hooks.add(new MythicItemHook());
        }
        hookAccessor = new HookAccessor(hooks);

        if (!load()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerShiftListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPickupListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDropListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerHitEntityListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DurabilityChangeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemBurnListener(this), this);

        PacketEvents.getAPI().init();

        CommandRegister.registerCommands(this);

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
                logSevere("Could not create the folder (please report this to the developers)! The plugin will shut down now.", exception);
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
        getWrapsLoader().unload();
    }

    private boolean loadMessages() {
        try {
            if (Files.notExists(MESSAGES_PATH)) {
                Files.copy(this.getClassLoader().getResourceAsStream("messages.properties"), MESSAGES_PATH);
            }
        } catch (IOException exception) {
            logSevere(
                    "Could not copy the configuration (please report this to the developers)! The plugin will shut down now.", exception);
            return false;
        }
        messageHandler = new MessageHandlerImpl(this);
        messageHandler.update(MESSAGES_PATH);
        return messageHandler.load(MESSAGES_PATH);
    }

    private boolean loadConfig() {
        try {
            if (Files.notExists(WRAP_FILES_PATH)) {
                Files.createDirectory(WRAP_FILES_PATH);
                Files.copy(getResource("silver_wraps.yml"), WRAP_FILES_PATH.resolve("silver_wraps.yml"));
            }
            if (Files.notExists(WRAP_FILES_PATH)) {
                Files.createDirectory(WRAP_FILES_PATH);
                Files.copy(getResource("emerald_wraps.yml"), WRAP_FILES_PATH.resolve("emerald_wraps.yml"));
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
            new ConfigFileTransformations().updateToLatest(CONFIG_PATH);
            CommentedConfiguration.loadConfiguration(CONFIG_PATH.toFile()).syncWithConfig(CONFIG_PATH.toFile(), getResource("config.yml"),
                   "items", "inventory.items", "collections", "unwrapper", "inventory.actions");
            config = LOADER.load().get(Config.class);
            getWrapsLoader().load();
        } catch (IOException exception) {
            logSevere("Could not load the configuration (please report this to the developers)! The plugin will shut down now.", exception);
            return false;
        }
        return true;
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
    public void logSevere(String message, Throwable thrown) {
        if (thrown != null) {
            getLogger().log(Level.SEVERE,
                    "\n=============================\n" +
                            message + "\n" +
                            "=============================", thrown);
        } else {
            getLogger().log(Level.SEVERE,
                    "\n=============================\n" +
                            message + "\n" +
                            "=============================");
        }
    }

    @Override
    public void logSevere(String message) {
        logSevere(message, null);
    }

    @Override
    public Config getConfiguration() {
        return config;
    }

    @Override
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public PreviewManager getPreviewManager() {
        return previewManager;
    }

    @Override
    public CollectionHelper getCollectionHelper() {
        return collectionHelper;
    }

    @Override
    public ActionHandler getActionHandler() {
        return actionHandler;
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
    public Storage<Player, List<Wrap>> getFavoriteWrapStorage() {
        return favoriteWrapStorage;
    }

    @Override
    public WrapsLoader getWrapsLoader() {
        return wrapsLoader;
    }

    @Override
    public HookAccessor getHookAccessor() {
        return hookAccessor;
    }

    public FileConverter getFileConverter() {
        return fileConverter;
    }

    public ContinuousUpdateChecker getUpdateChecker() {
        return updateChecker;
    }

}
