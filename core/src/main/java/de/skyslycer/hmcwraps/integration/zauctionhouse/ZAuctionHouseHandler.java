package de.skyslycer.hmcwraps.integration.zauctionhouse;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.IntegrationHandler;
import fr.maxlego08.zauctionhouse.api.blacklist.IBlacklistManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

public class ZAuctionHouseHandler implements IntegrationHandler {

    private static final String PLUGIN_NAME = "zAuctionHouseV3";

    private final HMCWraps plugin;
    private final WrapBlacklist wrapBlacklist;

    public ZAuctionHouseHandler(HMCWraps plugin) {
        this.plugin = plugin;
        this.wrapBlacklist = new WrapBlacklist(plugin);
    }

    @Override
    public void load() {
        if (!Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)) {
            return;
        }
        if (!plugin.getConfiguration().getPluginIntegrations().getzAuctionHouse().isEnabled()) {
            return;
        }
        var blacklistManager = getProvider(IBlacklistManager.class);
        if (blacklistManager == null) {
            plugin.getLogger().warning("zAuctionHouse integration is enabled, but the plugin does not provide the required API. Please report this to the developers of HMCWraps!");
            return;
        }
        blacklistManager.registerBlacklist(wrapBlacklist);
    }

    @Override
    public void unload() {
        if (!Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)) {
            return;
        }
        var blacklistManager = getProvider(IBlacklistManager.class);
        if (blacklistManager != null) {
            blacklistManager.unregisterBlacklist(wrapBlacklist);
        }
    }

    private <T> T getProvider(Class<T> classz) {
        RegisteredServiceProvider<T> provider = Bukkit.getServicesManager().getRegistration(classz);
        if (provider == null) return null;
        return provider.getProvider();
    }

}
