package de.skyslycer.hmcwraps.serialization.wrap.range;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class RangeSettings {

    private ValueRangeSettings<Integer> modelId;
    private ValueRangeSettings<String> color;
    private ValueRangeSettings<String> itemsadder;
    private ValueRangeSettings<String> oraxen;
    private ValueRangeSettings<String> mythic;
    private ValueRangeSettings<String> nexo;
    private ValueRangeSettings<String> executableItems;

    public RangeSettings(ValueRangeSettings<Integer> modelId, ValueRangeSettings<String> color, ValueRangeSettings<String> itemsAdder,
                         ValueRangeSettings<String> oraxen, ValueRangeSettings<String> mythic, ValueRangeSettings<String> nexo, ValueRangeSettings<String> executableItems) {
        this.modelId = modelId;
        this.color = color;
        this.itemsadder = itemsAdder;
        this.oraxen = oraxen;
        this.mythic = mythic;
        this.nexo = nexo;
        this.executableItems = executableItems;
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

    public ValueRangeSettings<String> getNexo() {
        return nexo;
    }

    public ValueRangeSettings<String> getExecutableItems() {
        return executableItems;
    }

    public static RangeSettings empty() {
        return new RangeSettings(new ValueRangeSettings<>(), new ValueRangeSettings<>(), new ValueRangeSettings<>(),
                new ValueRangeSettings<>(), new ValueRangeSettings<>(), new ValueRangeSettings<>(), new ValueRangeSettings<>());
    }

}
