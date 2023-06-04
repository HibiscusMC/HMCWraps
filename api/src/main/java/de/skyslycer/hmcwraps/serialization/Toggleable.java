package de.skyslycer.hmcwraps.serialization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Toggleable {

    private boolean enabled;

    public Toggleable(boolean enabled) {
        this.enabled = enabled;
    }

    public Toggleable() {
    }

    public boolean isEnabled() {
        return enabled;
    }

}
