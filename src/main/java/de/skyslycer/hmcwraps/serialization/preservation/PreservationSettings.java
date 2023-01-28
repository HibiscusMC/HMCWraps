package de.skyslycer.hmcwraps.serialization.preservation;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PreservationSettings implements IPreservationSettings {

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

    @Override
    public ValuePreservationSettings getModelId() {
        return modelId;
    }

    @Override
    public ValuePreservationSettings getColor() {
        return color;
    }

    @Override
    public ValuePreservationSettings getName() {
        return name;
    }

}
