package de.skyslycer.hmcwraps.integration;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.auctionguiplus.AuctionGuiPlusHandler;
import de.skyslycer.hmcwraps.integration.axauctions.AxAuctionsHandler;
import de.skyslycer.hmcwraps.integration.nexo.NexoHandler;
import de.skyslycer.hmcwraps.integration.zauctionhouse.ZAuctionHouseHandler;
import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AllIntegrationsHandler implements IntegrationHandler {

    private final HMCWraps plugin;

    private final Map<String, Class<? extends IntegrationHandler>> integrations = Map.of(
            "zAuctionHouseV3", ZAuctionHouseHandler.class,
            "AuctionGUIPlus", AuctionGuiPlusHandler.class,
            "Nexo", NexoHandler.class,
            "AxAuctions", AxAuctionsHandler.class
    );

    private final Map<String, IntegrationHandler> loadedIntegrations = new HashMap<>();

    public AllIntegrationsHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        integrations.forEach((name, handlerClass) -> {
            if (Bukkit.getPluginManager().isPluginEnabled(name)) {
                try {
                    IntegrationHandler handler = handlerClass.getConstructor(HMCWraps.class).newInstance(plugin);
                    handler.load();
                    loadedIntegrations.put(name, handler);
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load integration for " + name + ": " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void unload() {
        loadedIntegrations.forEach((name, handler) -> handler.unload());
    }

}
