package de.skyslycer.hmcwraps.integration.axauctions;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.IntegrationHandler;
import de.skyslycer.hmcwraps.integration.auctionguiplus.AuctionPreStartListener;
import net.brcdev.auctiongui.event.AuctionPreStartEvent;
import org.bukkit.Bukkit;

public class AxAuctionsHandler implements IntegrationHandler {

    private final HMCWraps plugin;

    private AxAuctionsPreSellListener listener = null;

    public AxAuctionsHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (!plugin.getConfiguration().getPluginIntegrations().getAuctionHouse().isEnabled()) {
            return;
        }
        listener = new AxAuctionsPreSellListener(plugin);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void unload() {
        if (listener != null) {
            AuctionPreStartEvent.getHandlerList().unregister(listener);
        }
    }

}