package de.skyslycer.hmcwraps.integration.axtrade;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.IntegrationHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class AxTradeHandler implements IntegrationHandler {

    private final HMCWraps plugin;

    private AxTradeCompleteListener listener = null;

    public AxTradeHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (!plugin.getConfiguration().getPluginIntegrations().getAuctionHouse().isTradeEnabled()) {
            return;
        }
        listener = new AxTradeCompleteListener(plugin);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void unload() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

}