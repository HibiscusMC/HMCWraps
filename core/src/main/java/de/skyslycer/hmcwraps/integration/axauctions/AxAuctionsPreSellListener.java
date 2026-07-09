package de.skyslycer.hmcwraps.integration.axauctions;

import com.artillexstudios.axauctions.api.events.AxAuctionsPreSellEvent;
import de.skyslycer.hmcwraps.HMCWraps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AxAuctionsPreSellListener implements Listener {

    private final HMCWraps plugin;

    public AxAuctionsPreSellListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreSell(AxAuctionsPreSellEvent event) {
        if (plugin.getConfiguration().getPluginIntegrations().getAuctionHouse().isBlacklisted(plugin, event.getItem())) {
            event.setCancelled(true);
        }
    }

}
