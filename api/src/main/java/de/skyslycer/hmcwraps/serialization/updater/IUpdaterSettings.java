package de.skyslycer.hmcwraps.serialization.updater;

import de.skyslycer.hmcwraps.serialization.IToggleable;
import de.skyslycer.hmcwraps.updater.PluginPlatform;

public interface IUpdaterSettings extends IToggleable {
    boolean isEnabled();

    PluginPlatform getPlatform();

    String getFrequency();

}
