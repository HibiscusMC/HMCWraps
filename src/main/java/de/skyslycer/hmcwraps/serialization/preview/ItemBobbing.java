package de.skyslycer.hmcwraps.serialization.preview;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ItemBobbing extends Toggleable {

    private double movement;
    private int times;

    public double getMovement() {
        return movement;
    }

    public int getTimes() {
        return times;
    }

}
