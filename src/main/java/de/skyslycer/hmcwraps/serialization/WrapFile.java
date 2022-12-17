package de.skyslycer.hmcwraps.serialization;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class WrapFile extends Toggleable implements IWrapFile {

    private Map<String, WrappableItem> items = new HashMap<>();

    public WrapFile(Map<String, WrappableItem> items, boolean enabled) {
        super(enabled);
        this.items = items;
    }

    public WrapFile() {}

    @Override
    public Map<String, IWrappableItem> getItems() {
        return new HashMap<>(items);
    }

}
