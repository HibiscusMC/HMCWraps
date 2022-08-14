package de.skyslycer.hmcwraps.serialization.preview;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class SneakCancel extends Toggleable implements ISneakCancel {

    private boolean actionBar;

    @Override
    public boolean isActionBar() {
        return actionBar;
    }

}
