package de.skyslycer.hmcwraps;

import com.tchristofferson.configupdater.ConfigUpdater;
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

    private static final YamlConfigurationLoader LOADER = YamlConfigurationLoader.builder()
            .path(CONFIG_PATH)
            .build();

    private Config config;

    @Override
    public void onEnable() {
        if (!checkDependency("ProtocolLib", true)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        checkDependency("PlaceholderAPI", false);
        checkDependency("ItemsAdder", false);
        checkDependency("Oraxen", false);

        saveResource("config.yml", false);
        try {
            ConfigUpdater.update(this, "config.yml", CONFIG_PATH.toFile(), "items.DIAMOND_SWORD", "inventory.items");
            config = LOADER.load().get(Config.class);
        } catch (IOException exception) {
            getLogger().severe(
                    "An error occurred while trying to load the config.yml (please report this to the developers):");
            exception.printStackTrace();
        }
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

}
