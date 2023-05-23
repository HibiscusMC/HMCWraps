package de.skyslycer.hmcwraps.serialization.preservation;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class PreservationSettings {

    private ValuePreservationSettings<Integer> modelId;
    private ValuePreservationSettings<String> color;
    private ValuePreservationSettings<String> name;
    private ValuePreservationSettings<List<String>> lore;
    private ValuePreservationSettings<List<String>> flags;

    public ValuePreservationSettings<Integer> getModelId() {
        return modelId;
    }

    public ValuePreservationSettings<String> getColor() {
        return color;
    }

    public ValuePreservationSettings<String> getName() {
        return name;
    }

    public ValuePreservationSettings<List<String>> getLore() {
        return lore;
    }

    public ValuePreservationSettings<List<String>> getFlags() {
        return flags;
    }

}
