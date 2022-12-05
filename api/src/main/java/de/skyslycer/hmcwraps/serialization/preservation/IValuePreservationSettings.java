package de.skyslycer.hmcwraps.serialization.preservation;

import java.util.Map;

public interface IValuePreservationSettings {

    Map<String, String> getDefaults();

    boolean isDefaultEnabled();

    boolean isOriginalEnabled();

}
