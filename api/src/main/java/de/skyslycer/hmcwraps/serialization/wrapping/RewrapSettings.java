package de.skyslycer.hmcwraps.serialization.wrapping;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class RewrapSettings {

    private boolean virtualEnabled = true;
    private boolean physicalEnabled = true;
    private boolean sameVirtualEnabled = true;
    private boolean samePhysicalEnabled = true;

    public boolean isVirtualEnabled() {
        return virtualEnabled;
    }

    public boolean isPhysicalEnabled() {
        return physicalEnabled;
    }

    public boolean isSameVirtualEnabled() {
        return sameVirtualEnabled;
    }

    public boolean isSamePhysicalEnabled() {
        return samePhysicalEnabled;
    }

}
