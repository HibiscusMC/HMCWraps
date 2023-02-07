package de.skyslycer.hmcwraps.serialization.range;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class RangeSettings {

    private ValueRangeSettings<Integer> modelId;
    private ValueRangeSettings<String> color;

    public RangeSettings(ValueRangeSettings<Integer> modelId, ValueRangeSettings<String> color) {
        this.modelId = modelId;
        this.color = color;
    }

    public RangeSettings() {
    }

    public ValueRangeSettings<Integer> getModelId() {
        return modelId;
    }

    public ValueRangeSettings<String> getColor() {
        return color;
    }

}
