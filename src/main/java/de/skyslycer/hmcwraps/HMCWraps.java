package de.skyslycer.hmcwraps;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class HMCWraps extends JavaPlugin {

    @Override
    public void onEnable() {
        if (!checkDependency("ProtocolLib", true)) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        if (checkDependency("PlaceholderAPI", false)) {
            getLogger().info("Plugin 'PlaceholderAPI' found. Initializing hook.");
        }
    }

    private boolean checkDependency(String name, boolean sendMessage) {
        if (!Bukkit.getPluginManager().isPluginEnabled(name)) {
            if (sendMessage) {
                getLogger().severe(
                        """
                                =============================
                                The plugin '""" + name + """
                                ' is a required dependency but was not found on this server! Please restart the server after you have added the missing plugin!
                                This plugin will shut down now.
                                ============================="""
                );
            }
            return false;
        }
        return true;
    }

}
