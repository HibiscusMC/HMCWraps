package de.skyslycer.hmcwraps.serialization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Toggleable implements IToggleable {

    private boolean enabled;

    public Toggleable(boolean enabled) {
        this.enabled = enabled;
    }

    public Toggleable() { }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
