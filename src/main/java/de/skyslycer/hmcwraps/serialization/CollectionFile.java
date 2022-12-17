package de.skyslycer.hmcwraps.serialization;

import java.util.List;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class CollectionFile extends Toggleable implements ICollectionsFile {

    private Map<String, List<String>> collections;

    public CollectionFile(Map<String, List<String>> collections, boolean enabled) {
        super(enabled);
        this.collections = collections;
    }

    public CollectionFile() { }

    @Override
    public Map<String, List<String>> getCollections() {
        return collections;
    }

}
