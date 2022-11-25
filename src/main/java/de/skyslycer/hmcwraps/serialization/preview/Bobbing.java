package de.skyslycer.hmcwraps.serialization.preview;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Bobbing extends Toggleable implements IBobbing {

    private double speed;
    private double intensity;

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public double getIntensity() {
        return intensity;
    }

}
