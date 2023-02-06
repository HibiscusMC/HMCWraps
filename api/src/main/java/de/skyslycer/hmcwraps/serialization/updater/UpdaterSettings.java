package de.skyslycer.hmcwraps.serialization.updater;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import de.skyslycer.hmcwraps.updater.PluginPlatform;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class UpdaterSettings extends Toggleable {
    private String frequency;
    private PluginPlatform platform;

    public PluginPlatform getPlatform() {
        return platform;
    }

    public String getFrequency() {
        return frequency;
    }

}
