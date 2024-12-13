package de.skyslycer.hmcwraps.serialization.globaldisable;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class GlobalDisable {

    private List<Integer> modelId;
    private List<String> color;
    private List<String> oraxen;
    private List<String> itemsadder;
    private List<String> mythic;
    private List<String> nexo;

    public List<Integer> getModelId() {
        return modelId;
    }

    public List<String> getColor() {
        return color;
    }

    public List<String> getOraxenId() {
        return oraxen;
    }

    public List<String> getItemsAdderId() {
        return itemsadder;
    }

    public List<String> getMythicId() {
        return mythic;
    }

    public List<String> getNexoId() {
        return nexo;
    }

}
