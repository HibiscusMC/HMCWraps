package de.skyslycer.hmcwraps.serialization.wrap.range;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class RangeSettings {

    private ValueRangeSettings<Integer> modelId;
    private ValueRangeSettings<String> color;
    private ValueRangeSettings<String> itemsadder;
    private ValueRangeSettings<String> oraxen;
    private ValueRangeSettings<String> mythic;

    public RangeSettings(ValueRangeSettings<Integer> modelId, ValueRangeSettings<String> color, ValueRangeSettings<String> itemsAdder, ValueRangeSettings<String> oraxen, ValueRangeSettings<String> mythic) {
        this.modelId = modelId;
        this.color = color;
        this.itemsadder = itemsAdder;
        this.oraxen = oraxen;
        this.mythic = mythic;
    }

    public RangeSettings() {}

    public ValueRangeSettings<Integer> getModelId() {
        return modelId;
    }

    public ValueRangeSettings<String> getColor() {
        return color;
    }

    public ValueRangeSettings<String> getItemsAdder() {
        return itemsadder;
    }

    public ValueRangeSettings<String> getOraxen() {
        return oraxen;
    }

    public ValueRangeSettings<String> getMythic() {
        return mythic;
    }

    public static RangeSettings empty() {
        return new RangeSettings(new ValueRangeSettings<>(), new ValueRangeSettings<>(), new ValueRangeSettings<>(),
                new ValueRangeSettings<>(), new ValueRangeSettings<>());
    }

}
