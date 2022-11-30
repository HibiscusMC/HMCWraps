package de.skyslycer.hmcwraps.serialization;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class WrapFile implements IWrapFile {

    private boolean enabled = true;
    private Map<String, WrappableItem> items = new HashMap<>();

    @Override
    public Map<String, IWrappableItem> getItems() {
        return new HashMap<>(items);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
