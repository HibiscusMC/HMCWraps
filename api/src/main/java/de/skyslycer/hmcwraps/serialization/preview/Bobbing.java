package de.skyslycer.hmcwraps.serialization.preview;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class Bobbing extends Toggleable {

    private double speed;
    private double intensity;

    public double getSpeed() {
        return speed;
    }

    public double getIntensity() {
        return intensity;
    }

}
