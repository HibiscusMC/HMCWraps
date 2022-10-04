package de.skyslycer.hmcwraps;

import com.github.retrooper.packetevents.PacketEvents;
import com.tchristofferson.configupdater.ConfigUpdater;
import de.skyslycer.hmcwraps.circle.CircleManager;
import de.skyslycer.hmcwraps.commands.WrapCommand;
import de.skyslycer.hmcwraps.itemhook.ItemHook;
import de.skyslycer.hmcwraps.itemhook.ItemsAdderItemHook;
import de.skyslycer.hmcwraps.itemhook.OraxenItemHook;
import de.skyslycer.hmcwraps.listener.InventoryClickListener;
import de.skyslycer.hmcwraps.listener.PlayerInteractListener;
import de.skyslycer.hmcwraps.listener.PlayerPickupListener;
import de.skyslycer.hmcwraps.listener.PlayerShiftListener;
import de.skyslycer.hmcwraps.messages.IMessageHandler;
import de.skyslycer.hmcwraps.messages.MessageHandler;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.preview.IPreviewManager;
import de.skyslycer.hmcwraps.preview.PreviewManager;
import de.skyslycer.hmcwraps.serialization.Config;
import de.skyslycer.hmcwraps.serialization.IConfig;
import de.skyslycer.hmcwraps.serialization.IWrap;
import de.skyslycer.hmcwraps.serialization.IWrappableItem;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.wrap.CollectionHelper;
import de.skyslycer.hmcwraps.wrap.ICollectionHelper;
import de.skyslycer.hmcwraps.wrap.IWrapper;
import de.skyslycer.hmcwraps.wrap.Wrapper;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;

public class HMCWraps extends JavaPlugin implements IHMCWraps {

    private static final YamlConfigurationLoader LOADER = YamlConfigurationLoader.builder()
            .path(CONFIG_PATH)
            .build();
    private final Set<ItemHook> hooks = new HashSet<>();
    private final Map<String, IWrap> wraps = new HashMap<>();
    private final Set<String> loadedHooks = new HashSet<>();
    private final IWrapper wrapper = new Wrapper(this);
    private final IPreviewManager previewManager = new PreviewManager(this);
    private final ICollectionHelper collection = new CollectionHelper(this);
    private final CircleManager circleManager = new CircleManager();
    private IConfig config;
    private IMessageHandler handler;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
        PacketEvents.getAPI().getSettings().checkForUpdates(false);
    }

    @Override
    public void onEnable() {
        if (!load()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerShiftListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPickupListener(this), this);

        PacketEvents.getAPI().init();

        registerCommands();

        new PluginMetrics(this).init();
    }

    @Override
    public void onDisable() {
        unload();
        PacketEvents.getAPI().terminate();
    }

    @Override
    public boolean load() {
        checkDependency("PlaceholderAPI", false);
        if (checkDependency("ItemsAdder", false)) {
            hooks.add(new ItemsAdderItemHook());
        }
        if (checkDependency("Oraxen", false)) {
            hooks.add(new OraxenItemHook());
        }

        if (!Files.exists(PLUGIN_PATH)) {
            try {
                Files.createDirectory(PLUGIN_PATH);
            } catch (IOException exception) {
                logSevere(
                        "Could not create the folder (please report this to the developers)! The plugin will shut down now.");
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

        getConfiguration().getItems()
                .forEach((ignored, wrappableItem) -> wrappableItem.getWraps().forEach((id, wrap) -> wraps.put(wrap.getUuid(), wrap)));
        wraps.remove("-");
        getPreviewManager().removeAll(true);
        return true;
    }

    @Override
    public void unload() {
        hooks.clear();
        wraps.clear();
    }

    private void registerCommands() {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);

        commandHandler.registerValueResolver(Wrap.class, context -> {
            var wrap = (Wrap) getWraps().get(context.pop());
            if (wrap == null) {
                getHandler().send(context.actor().as(BukkitActor.class).getAsPlayer(), Messages.COMMAND_INVALID_WRAP,
                        Placeholder.parsed("uuid", context.pop()));
                throw new IllegalArgumentException();
            }
            return wrap;
        });
        commandHandler.getAutoCompleter().registerParameterSuggestions(Wrap.class, SuggestionProvider.map(() -> getWraps().values(), IWrap::getUuid));
        commandHandler.registerExceptionHandler(NoPermissionException.class,
                (actor, context) -> getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.NO_PERMISSION));
        commandHandler.registerExceptionHandler(SenderNotPlayerException.class,
                (actor, context) -> getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_PLAYER_ONLY));
        commandHandler.registerExceptionHandler(MissingArgumentException.class,
                (actor, context) -> getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_MISSING_ARGUMENT,
                        Placeholder.parsed("argument", context.getParameter().getName())));
        commandHandler.disableStackTraceSanitizing();
        commandHandler.setHelpWriter(
                (command, actor) -> getHandler().get(Messages.COMMAND_HELP_FORMAT).replace("<command>", command.getPath().toRealString())
                        .replace("<usage>", command.getUsage()).replace("<description>", command.getDescription()));
        commandHandler.register(new WrapCommand(this));
        commandHandler.registerBrigadier();
    }

    private boolean loadMessages() {
        try {
            if (!Files.exists(MESSAGES_PATH)) {
                Files.copy(this.getClassLoader().getResourceAsStream("messages.properties"), MESSAGES_PATH);
            }
        } catch (IOException exception) {
            logSevere(
                    "Could not copy the configuration (please report this to the developers)! The plugin will shut down now.");
            exception.printStackTrace();
            return false;
        }
        handler = new MessageHandler(this);
        handler.update(MESSAGES_PATH);
        return handler.load(MESSAGES_PATH);
    }

    private boolean loadConfig() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                Files.copy(getResource("config.yml"), CONFIG_PATH);
            }
            ConfigUpdater.update(this, "config.yml", CONFIG_PATH.toFile(), "items", "inventory.items", "collections");
            config = LOADER.load().get(Config.class);
        } catch (IOException exception) {
            logSevere(
                    "Could not load the configuration (please report this to the developers)! The plugin will shut down now.");
            exception.printStackTrace();
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
        for (IWrappableItem item : getConfiguration().getItems().values()) {
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
    public IMessageHandler getHandler() {
        return handler;
    }

    @Override
    @NotNull
    public Map<String, IWrap> getWraps() {
        return wraps;
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
    public ICollectionHelper getCollection() {
        return collection;
    }

    @NotNull
    public CircleManager getCircleManager() {
        return circleManager;
    }

}
