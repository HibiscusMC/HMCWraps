package de.skyslycer.hmcwraps.serialization.files;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public class CollectionFile extends Toggleable {

    private Map<String, List<String>> collections;

    public CollectionFile(Map<String, List<String>> collections, boolean enabled) {
        super(enabled);
        this.collections = collections;
    }

    public CollectionFile() {
    }

    public Map<String, List<String>> getCollections() {
        return collections;
    }

}
