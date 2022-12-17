package de.skyslycer.hmcwraps.serialization.preview;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreviewSettings implements IPreviewSettings {

    private int duration;
    private int rotation;
    private SneakCancel sneakCancel;
    private Bobbing bobbing;

    public PreviewSettings(int duration, int rotation, SneakCancel sneakCancel, Bobbing bobbing) {
        this.duration = duration;
        this.rotation = rotation;
        this.sneakCancel = sneakCancel;
        this.bobbing = bobbing;
    }

    public PreviewSettings() { }

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
