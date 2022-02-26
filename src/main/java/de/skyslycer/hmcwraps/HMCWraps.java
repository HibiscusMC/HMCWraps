package de.skyslycer.hmcwraps;

import com.tchristofferson.configupdater.ConfigUpdater;
import de.skyslycer.hmcwraps.messages.MessageHandler;
import de.skyslycer.hmcwraps.serialization.Config;
import java.io.IOException;
import java.nio.file.Path;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

public class HMCWraps extends JavaPlugin {

    public static final Path PLUGIN_PATH = Path.of("plugins", "HMCWraps");
    public static final Path CONFIG_PATH = PLUGIN_PATH.resolve("config.yml");
    public static final Path MESSAGES_PATH = PLUGIN_PATH.resolve("messages.properties");

    private static final YamlConfigurationLoader LOADER = YamlConfigurationLoader.builder()
            .path(CONFIG_PATH)
            .build();

    private Config config;
    private MessageHandler handler;

    @Override
    public void onEnable() {
        if (!checkDependency("ProtocolLib", true)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        checkDependency("PlaceholderAPI", false);
        checkDependency("ItemsAdder", false);
        checkDependency("Oraxen", false);

        if (!loadConfig()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (!loadMessages()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
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

    @NotNull
    public Config getConfiguration() {
        return config;
    }

    @NotNull
    public MessageHandler getHandler() {
        return handler;
    }

}
