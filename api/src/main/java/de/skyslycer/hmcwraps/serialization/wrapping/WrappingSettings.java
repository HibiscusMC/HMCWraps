package de.skyslycer.hmcwraps.serialization.wrapping;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class WrappingSettings {

    private RewrapSettings rewrap;

    public RewrapSettings getRewrap() {
        return rewrap;
    }

}
