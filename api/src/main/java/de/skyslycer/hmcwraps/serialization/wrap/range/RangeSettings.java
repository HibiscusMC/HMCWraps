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
    private ValueRangeSettings<String> craftEngine;
    private ValueRangeSettings<String> mmoItems;
    private ValueRangeSettings<String> mmoItemsType;

    public RangeSettings(ValueRangeSettings<Integer> modelId, ValueRangeSettings<String> color, ValueRangeSettings<String> itemsAdder,
                         ValueRangeSettings<String> oraxen, ValueRangeSettings<String> mythic, ValueRangeSettings<String> nexo,
                         ValueRangeSettings<String> executableItems, ValueRangeSettings<String> craftEngine, ValueRangeSettings<String> mmoItems,
                         ValueRangeSettings<String> mmoItemsType) {
        this.modelId = modelId;
        this.color = color;
        this.itemsadder = itemsAdder;
        this.oraxen = oraxen;
        this.mythic = mythic;
        this.nexo = nexo;
        this.executableItems = executableItems;
        this.craftEngine = craftEngine;
        this.mmoItems = mmoItems;
        this.mmoItemsType = mmoItemsType;
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

    public ValueRangeSettings<String> getCraftEngine() {
        return craftEngine;
    }

    public ValueRangeSettings<String> getMmoItems() {
        return mmoItems;
    }

    public ValueRangeSettings<String> getMmoItemsType() {
        return mmoItemsType;
    }

    public static RangeSettings empty() {
        return new RangeSettings(new ValueRangeSettings<>(), new ValueRangeSettings<>(), new ValueRangeSettings<>(),
                new ValueRangeSettings<>(), new ValueRangeSettings<>(), new ValueRangeSettings<>(),
                new ValueRangeSettings<>(), new ValueRangeSettings<>(), new ValueRangeSettings<>(),
                new ValueRangeSettings<>());
    }

}
