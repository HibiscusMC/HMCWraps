package de.skyslycer.hmcwraps.serialization;

import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class WrappableItem {

    private Map<String, Wrap> wraps;

    public Map<String, Wrap> getWraps() {
        return wraps;
    }

}
