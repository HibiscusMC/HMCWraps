package de.skyslycer.hmcwraps.serialization.preview;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class SneakCancel extends Toggleable implements ISneakCancel {

    private boolean actionBar;

    public SneakCancel(boolean actionBar, boolean enabled) {
        super(enabled);
        this.actionBar = actionBar;
    }

    public SneakCancel() {
    }

    @Override
    public boolean isActionBar() {
        return actionBar;
    }

}
