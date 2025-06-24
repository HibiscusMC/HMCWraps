package de.skyslycer.hmcwraps.integration;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.zauctionhouse.ZAuctionHouseHandler;

import java.util.List;

public class AllIntegrationsHandler implements IntegrationHandler {

    private final List<IntegrationHandler> handlers;

    public AllIntegrationsHandler(HMCWraps plugin) {
        this.handlers = List.of(new ZAuctionHouseHandler(plugin));
    }

    @Override
    public void load() {
        for (IntegrationHandler handler : handlers) {
            handler.load();
        }
    }

    @Override
    public void unload() {
        for (IntegrationHandler handler : handlers) {
            handler.unload();
        }
    }

}
