package de.skyslycer.hmcwraps.serialization.integration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PluginIntegrations {

    private ZAuctionHouseIntegration zAuctionHouse;

    public ZAuctionHouseIntegration getzAuctionHouse() {
        return zAuctionHouse;
    }

}
