package de.skyslycer.hmcwraps.serialization.preservation;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreservationSettings {

    private ValuePreservationSettings modelId;
    private ValuePreservationSettings color;
    private ValuePreservationSettings name;

    public PreservationSettings(ValuePreservationSettings modelId, ValuePreservationSettings color, ValuePreservationSettings name) {
        this.modelId = modelId;
        this.color = color;
        this.name = name;
    }

    public PreservationSettings() {
    }

    public ValuePreservationSettings getModelId() {
        return modelId;
    }

    public ValuePreservationSettings getColor() {
        return color;
    }

    public ValuePreservationSettings getName() {
        return name;
    }

}
