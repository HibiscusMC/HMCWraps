package de.skyslycer.hmcwraps.serialization.wrapping;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class WrappingSettings {

    private RewrapSettings rewrap;
    private boolean makeWrappersUnstackable;
    private boolean giveWrapperAfterBreaking;

    public RewrapSettings getRewrap() {
        return rewrap;
    }

    public boolean isMakeWrappersUnstackable() {
        return makeWrappersUnstackable;
    }

    public boolean isGiveWrapperAfterBreaking() {
        return giveWrapperAfterBreaking;
    }

}
