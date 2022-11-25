package de.skyslycer.hmcwraps.serialization.preview;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreviewSettings implements IPreviewSettings {

    private int duration;
    private int rotation;
    private SneakCancel sneakCancel;
    private Bobbing bobbing;

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getRotation() {
        return rotation;
    }

    @Override
    public SneakCancel getSneakCancel() {
        return sneakCancel;
    }

    @Override
    public IBobbing getBobbing() {
        return bobbing;
    }

}
