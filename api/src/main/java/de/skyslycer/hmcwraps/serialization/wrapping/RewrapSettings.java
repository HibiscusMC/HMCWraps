package de.skyslycer.hmcwraps.serialization.wrapping;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class RewrapSettings {

    private boolean virtualEnabled = true;
    private boolean physicalEnabled = true;

    public boolean isVirtualEnabled() {
        return virtualEnabled;
    }

    public boolean isPhysicalEnabled() {
        return physicalEnabled;
    }

}
