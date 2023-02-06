package de.skyslycer.hmcwraps.serialization.preview;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreviewSettings {

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

    public PreviewSettings() {
    }

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
