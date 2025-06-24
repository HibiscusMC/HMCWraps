package de.skyslycer.hmcwraps.serialization.integration;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ZAuctionHouseIntegration extends Toggleable {

    private boolean blacklistVirtual;
    private boolean blacklistPhysical;
    private List<String> blacklistedWraps;

    public boolean isBlacklistVirtual() {
        return blacklistVirtual;
    }

    public boolean isBlacklistPhysical() {
        return blacklistPhysical;
    }

    public List<String> getBlacklistedWraps() {
        return blacklistedWraps;
    }

}
