package de.skyslycer.hmcwraps.serialization.preview;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreviewSettings {

    private int duration;
    private int rotation;
    private SneakCancel sneakCancel;
    private Bobbing bobbing;

    public int getDuration() {
        return duration;
    }

    public int getRotation() {
        return rotation;
    }

    public SneakCancel getSneakCancel() {
        return sneakCancel;
    }

    public Bobbing getBobbing() {
        return bobbing;
    }

}
