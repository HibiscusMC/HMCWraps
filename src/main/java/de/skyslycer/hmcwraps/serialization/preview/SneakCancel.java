package de.skyslycer.hmcwraps.serialization.preview;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class SneakCancel {

    private boolean enabled;
    private boolean actionBar;

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isActionBar() {
        return actionBar;
    }

}
