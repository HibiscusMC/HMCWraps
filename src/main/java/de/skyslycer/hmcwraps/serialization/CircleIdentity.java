package de.skyslycer.hmcwraps.serialization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class CircleIdentity extends Toggleable {

    private double size;
    private double amplitude;

    public double getSize() {
        return size;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public static CircleIdentity build(int size, int amplitude) {
        var identity =  new CircleIdentity();
        identity.size = size;
        identity.amplitude = amplitude;
        return identity;
    }

}
