package de.skyslycer.hmcwraps.serialization.preview;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreviewSettings implements IPreviewSettings {

    private int duration;
    private int rotation;
    private int bob;
    private double bobHeightMultiplier;
    private SneakCancel sneakCancel;

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getRotation() {
        return rotation;
    }

    @Override
    public int getBob() {
        return bob;
    }

    @Override
    public double getBobHeightMultiplier() {
        return bobHeightMultiplier;
    }

    @Override
    public SneakCancel getSneakCancel() {
        return sneakCancel;
    }

}
