package de.skyslycer.hmcwraps.serialization.preservation;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreservationSettings implements IPreservationSettings {

    ValuePreservationSettings modelId;
    ValuePreservationSettings color;

    @Override
    public ValuePreservationSettings getModelId() {
        return modelId;
    }

    @Override
    public ValuePreservationSettings getColor() {
        return color;
    }

}
