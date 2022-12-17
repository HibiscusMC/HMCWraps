package de.skyslycer.hmcwraps.serialization.preview;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Bobbing extends Toggleable implements IBobbing {

    private double speed;
    private double intensity;

    public Bobbing(double speed, double intensity, boolean enabled) {
        super(enabled);
        this.speed = speed;
        this.intensity = intensity;
    }

    public Bobbing() { }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getIntensity() {
        return intensity;
    }

}
