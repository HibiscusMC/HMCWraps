package de.skyslycer.hmcwraps.serialization.wrap;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class WrappableItem {

    private Map<String, Wrap> wraps = new HashMap<>();

    public WrappableItem(Map<String, Wrap> wraps) {
        this.wraps = wraps;
    }

    public WrappableItem() {
    }

    public Map<String, Wrap> getWraps() {
        return wraps;
    }

    public void putWrap(String name, Wrap wrap) {
        wraps.put(name, wrap);
    }

}
