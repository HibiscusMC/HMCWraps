package de.skyslycer.hmcwraps.serialization.preservation;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreservationSettings implements IPreservationSettings {

    ValuePreservationSettings modelId;
    ValuePreservationSettings color;

    public PreservationSettings(ValuePreservationSettings modelId, ValuePreservationSettings color) {
        this.modelId = modelId;
        this.color = color;
    }

    public PreservationSettings() {
    }

    @Override
    public ValuePreservationSettings getModelId() {
        return modelId;
    }

    @Override
    public ValuePreservationSettings getColor() {
        return color;
    }

}
