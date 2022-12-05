package de.skyslycer.hmcwraps.serialization.preservation;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ValuePreservationSettings implements IValuePreservationSettings {

    private boolean defaultEnabled;
    private boolean originalEnabled;
    private Map<String, String> defaults = new HashMap<>();

    @Override
    public Map<String, String> getDefaults() {
        return defaults;
    }

    @Override
    public boolean isDefaultEnabled() {
        return defaultEnabled;
    }

    @Override
    public boolean isOriginalEnabled() {
        return originalEnabled;
    }

}
