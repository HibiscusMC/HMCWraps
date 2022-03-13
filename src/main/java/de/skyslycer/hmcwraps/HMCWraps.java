package de.skyslycer.hmcwraps;

import com.github.retrooper.packetevents.PacketEvents;
import com.tchristofferson.configupdater.ConfigUpdater;
import de.skyslycer.hmcwraps.commands.WrapCommand;
import de.skyslycer.hmcwraps.itemhook.ItemHook;
import de.skyslycer.hmcwraps.itemhook.ItemsAdderItemHook;
import de.skyslycer.hmcwraps.itemhook.OraxenItemHook;
import de.skyslycer.hmcwraps.listener.InventoryClickListener;
import de.skyslycer.hmcwraps.listener.PlayerInteractListener;
import de.skyslycer.hmcwraps.listener.PlayerShiftListener;
import de.skyslycer.hmcwraps.messages.MessageHandler;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.preview.PreviewManager;
import de.skyslycer.hmcwraps.serialization.Config;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.serialization.WrappableItem;
import de.skyslycer.hmcwraps.wrap.Wrapper;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.NoPermissionException;

public class HMCWraps extends JavaPlugin {

    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static final Path PLUGIN_PATH = Path.of("plugins", "HMCWraps");
    public static final Path CONFIG_PATH = PLUGIN_PATH.resolve("config.yml");
    private static final YamlConfigurationLoader LOADER = YamlConfigurationLoader.builder()
            .path(CONFIG_PATH)
            .build();
    public static final Path MESSAGES_PATH = PLUGIN_PATH.resolve("messages.properties");
    private final Set<ItemHook> hooks = new HashSet<>();
    private final Map<String, Wrap> wraps = new HashMap<>();
    private final Set<String> loadedHooks = new HashSet<>();
    private final Wrapper wrapper = new Wrapper(this);
    private final PreviewManager previewManager = new PreviewManager(this);
    private Config config;
    private MessageHandler handler;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
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

        PacketEvents.getAPI().init();

        registerCommands();

        new PluginMetrics(this).init();
    }

    @Override
    public void onDisable() {
        unload();
        PacketEvents.getAPI().terminate();
    }

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

    public void unload() {
        hooks.clear();
        wraps.clear();
    }

    private void registerCommands() {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(this);

        commandHandler.registerValueResolver(Wrap.class, context -> {
            var wrap = getWraps().get(context.pop());
            if (wrap == null) {
                getHandler().send(context.actor().as(BukkitActor.class).getAsPlayer(), Messages.COMMAND_INVALID_WRAP, Placeholder.parsed("uuid", context.pop()));
                throw new IllegalArgumentException();
            }
            return wrap;
        });
        commandHandler.registerExceptionHandler(NoPermissionException.class,
                (actor, context) -> getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.NO_PERMISSION));
        commandHandler.registerExceptionHandler(SenderNotPlayerException.class,
                (actor, context) -> getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_PLAYER_ONLY));
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
            ConfigUpdater.update(this, "config.yml", CONFIG_PATH.toFile(), "items", "inventory.items");
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

    public ItemStack getItemFromHook(String id) {
        var possible = hooks.stream().filter(it -> id.startsWith(it.getPrefix())).findFirst();
        if (possible.isEmpty()) {
            return ItemHook.defaultHook.get(id);
        } else {
            return possible.get().get(id.replace(possible.get().getPrefix(), ""));
        }
    }

    public int getModelIdFromHook(String id) {
        try {
            return Integer.parseInt(id);
        } catch (NumberFormatException ignored) {
            var possible = hooks.stream().filter(it -> id.startsWith(it.getPrefix())).findFirst();
            return possible.map(itemHook -> itemHook.getModelId(id.replace(possible.get().getPrefix(), ""))).orElse(-1);
        }
    }

    public void logSevere(String message) {
        getLogger().severe(
                "\n=============================\n" +
                        message + "\n" +
                        "============================="
        );
    }

    public int getWrapAmount() {
        int count = 0;
        for (WrappableItem item : getConfiguration().getItems().values()) {
            count += item.getWraps().size();
        }
        return count;
    }

    @NotNull
    public Config getConfiguration() {
        return config;
    }

    @NotNull
    public MessageHandler getHandler() {
        return handler;
    }

    @NotNull
    public Map<String, Wrap> getWraps() {
        return wraps;
    }

    @NotNull
    public Wrapper getWrapper() {
        return wrapper;
    }

    @NotNull
    public PreviewManager getPreviewManager() {
        return previewManager;
    }

}
