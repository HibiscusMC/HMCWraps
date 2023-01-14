package de.skyslycer.hmcwraps.serialization.preservation;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public class ValuePreservationSettings implements IValuePreservationSettings {

    private boolean defaultEnabled;
    private boolean originalEnabled;
    private Map<String, String> defaults = new HashMap<>();

    public ValuePreservationSettings(boolean defaultEnabled, boolean originalEnabled, Map<String, String> defaults) {
        this.defaultEnabled = defaultEnabled;
        this.originalEnabled = originalEnabled;
        this.defaults = defaults;
    }

    public ValuePreservationSettings() {
    }

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
