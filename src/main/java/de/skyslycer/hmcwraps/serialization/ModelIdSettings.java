package de.skyslycer.hmcwraps.serialization;

import java.util.HashMap;
import java.util.Map;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ModelIdSettings implements IModelIdSettings {

    private boolean defaultModelIdsEnabled;
    private boolean originalModelIdsEnabled;
    private Map<String, Integer> defaultModelIds = new HashMap<>();

    @Override
    public Map<String, Integer> getDefaultModelIds() {
        return defaultModelIds;
    }

    @Override
    public boolean isDefaultModelIdsEnabled() {
        return defaultModelIdsEnabled;
    }

    @Override
    public boolean isOriginalModelIdsEnabled() {
        return originalModelIdsEnabled;
    }

}
