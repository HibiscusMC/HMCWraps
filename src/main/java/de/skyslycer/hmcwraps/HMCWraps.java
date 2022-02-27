package de.skyslycer.hmcwraps;

import com.tchristofferson.configupdater.ConfigUpdater;
import de.skyslycer.hmcwraps.commands.WrapCommand;
import de.skyslycer.hmcwraps.itemhook.ItemHook;
import de.skyslycer.hmcwraps.itemhook.ItemsAdderItemHook;
import de.skyslycer.hmcwraps.itemhook.OraxenItemHook;
import de.skyslycer.hmcwraps.listener.InventoryClickListener;
import de.skyslycer.hmcwraps.listener.PlayerInteractListener;
import de.skyslycer.hmcwraps.messages.MessageHandler;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.Config;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.wrap.Wrapper;
import java.io.IOException;
import java.nio.file.Path;
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
import revxrsal.commands.bukkit.BukkitCommandHandler;
import revxrsal.commands.bukkit.core.BukkitActor;
import revxrsal.commands.bukkit.exception.SenderNotPlayerException;
import revxrsal.commands.exception.NoPermissionException;

public class HMCWraps extends JavaPlugin {

    public static final Path PLUGIN_PATH = Path.of("plugins", "HMCWraps");
    public static final Path CONFIG_PATH = PLUGIN_PATH.resolve("config.yml");
    public static final Path MESSAGES_PATH = PLUGIN_PATH.resolve("messages.properties");

    private static final YamlConfigurationLoader LOADER = YamlConfigurationLoader.builder()
            .path(CONFIG_PATH)
            .build();

    private Config config;
    private MessageHandler handler;

    private final Set<ItemHook> hooks = new HashSet<>();
    private final Map<String, Wrap> wraps = new HashMap<>();

    private final Wrapper wrapper = new Wrapper(this);

    @Override
    public void onEnable() {
        if (!checkDependency("ProtocolLib", true)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        checkDependency("PlaceholderAPI", false);
        if (checkDependency("ItemsAdder", false)) {
            hooks.add(new ItemsAdderItemHook());
        }
        if (checkDependency("Oraxen", false)) {
            hooks.add(new OraxenItemHook());
        }

        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(this), this);

        load();
        registerCommands();
    }

    @Override
    public void onDisable() {
        unload();
    }

    public void load() {
        if (!loadConfig()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!loadMessages()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getConfiguration().getItems().forEach((ignored, wrappableItem) ->
                wrappableItem.getWraps().forEach((id, wrap) -> wraps.put(wrap.getUuid(), wrap)));
        wraps.remove("-");
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
                getHandler().send(context.actor().as(BukkitActor.class).getAsPlayer(), Messages.COMMAND_INVALID_WRAP,
                        Placeholder.parsed("%uuid%", context.pop()));
                throw new IllegalArgumentException();
            }
            return wrap;
        });
        commandHandler.registerExceptionHandler(NoPermissionException.class, (actor, context) -> getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.NO_PERMISSION));
        commandHandler.registerExceptionHandler(
                SenderNotPlayerException.class, (actor, context) -> getHandler().send(actor.as(BukkitActor.class).getSender(), Messages.COMMAND_PLAYER_ONLY));
        commandHandler.register(new WrapCommand(this));
    }

    private boolean loadMessages() {
        saveResource(MESSAGES_PATH.getFileName().toString(), false);
        handler = new MessageHandler(this);
        return handler.load(MESSAGES_PATH);
    }

    private boolean loadConfig() {
        saveResource(CONFIG_PATH.getFileName().toString(), false);
        try {
            ConfigUpdater.update(this, "config.yml", CONFIG_PATH.toFile(), "items.DIAMOND_SWORD", "inventory.items");
            config = LOADER.load().get(Config.class);
        } catch (IOException exception) {
            getLogger().severe("""
                    =============================
                    Could not load the configuration (please report this to the developers)! The plugin will shut down now.
                    =============================
                    """
            );
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean checkDependency(String name, boolean needed) {
        if (!Bukkit.getPluginManager().isPluginEnabled(name)) {
            if (needed) {
                getLogger().severe(
                        """
                                =============================
                                The plugin '""" + name + """
                                ' is a required dependency but was not found on this server! Please restart the server after you have added the missing plugin!
                                This plugin will shut down now.
                                ============================="""
                );
            } else {
                getLogger().info("Plugin '" + name + "' found. Initializing hook.");
            }
            return false;
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

    public int getModellIdFromHook(String id) {
        var possible = hooks.stream().filter(it -> id.startsWith(it.getPrefix())).findFirst();
        return possible.map(itemHook -> itemHook.getModellId(id)).orElse(-1);
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

}
