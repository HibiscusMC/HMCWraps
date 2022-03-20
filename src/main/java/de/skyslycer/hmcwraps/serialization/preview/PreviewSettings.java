package de.skyslycer.hmcwraps.serialization.preview;

import de.skyslycer.hmcwraps.serialization.CircleIdentity;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreviewSettings {

    private int duration;
    private int rotation;
    private CircleIdentity bobbing;
    private SneakCancel sneakCancel;

    public int getDuration() {
        return duration;
    }

    public int getRotation() {
        return rotation;
    }

    public CircleIdentity getBobbing() {
        return bobbing;
    }

    public SneakCancel getSneakCancel() {
        return sneakCancel;
    }

}
