package de.skyslycer.hmcwraps.serialization;

import java.util.Map;

public interface IModelIdSettings {

    Map<String, Integer> getDefaultModelIds();

    boolean isDefaultModelIdsEnabled();

    boolean isOriginalModelIdsEnabled();

}
