package de.skyslycer.hmcwraps.integration;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.zauctionhouse.ZAuctionHouseHandler;
import org.bukkit.Bukkit;

public class AllIntegrationsHandler implements IntegrationHandler {

    private static final String AUCTION_HOUSE = "zAuctionHouseV3";

    private final HMCWraps plugin;

    private IntegrationHandler auctionHouse;

    public AllIntegrationsHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (Bukkit.getPluginManager().isPluginEnabled(AUCTION_HOUSE)) {
            auctionHouse = new ZAuctionHouseHandler(plugin);
            auctionHouse.load();
        }
    }

    @Override
    public void unload() {
        if (Bukkit.getPluginManager().isPluginEnabled(AUCTION_HOUSE) && auctionHouse != null) {
            auctionHouse.unload();
        }
    }

}
