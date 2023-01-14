package de.skyslycer.hmcwraps.serialization;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class CollectionFile extends Toggleable implements ICollectionFile {

    private Map<String, List<String>> collections;

    public CollectionFile(Map<String, List<String>> collections, boolean enabled) {
        super(enabled);
        this.collections = collections;
    }

    public CollectionFile() {
    }

    @Override
    public Map<String, List<String>> getCollections() {
        return collections;
    }

}
