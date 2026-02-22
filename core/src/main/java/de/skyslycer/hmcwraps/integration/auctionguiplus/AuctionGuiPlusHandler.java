package de.skyslycer.hmcwraps.integration.auctionguiplus;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.IntegrationHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class AuctionGuiPlusHandler implements IntegrationHandler {

    private final HMCWraps plugin;

    private AuctionPreStartListener listener = null;

    public AuctionGuiPlusHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (!plugin.getConfiguration().getPluginIntegrations().getAuctionHouse().isEnabled()) {
            return;
        }
        listener = new AuctionPreStartListener(plugin);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void unload() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

}
