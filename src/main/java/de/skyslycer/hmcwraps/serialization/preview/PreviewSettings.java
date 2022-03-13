package de.skyslycer.hmcwraps.serialization.preview;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreviewSettings {

    private int duration;
    private int rotation;
    private ItemBobbing bobbing;
    private SneakCancel sneakCancel;

    public int getDuration() {
        return duration;
    }

    public int getRotation() {
        return rotation;
    }

    public ItemBobbing getBobbing() {
        return bobbing;
    }

    public SneakCancel getSneakCancel() {
        return sneakCancel;
    }

}
