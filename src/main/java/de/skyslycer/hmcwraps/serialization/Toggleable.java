package de.skyslycer.hmcwraps.serialization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Toggleable implements IToggleable {

    private boolean enabled;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
