package de.skyslycer.hmcwraps.serialization.updater;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import de.skyslycer.hmcwraps.updater.PluginPlatform;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class UpdaterSettings extends Toggleable implements IUpdaterSettings {
    private String frequency;
    private PluginPlatform platform;

    @Override
    public PluginPlatform getPlatform() {
        return platform;
    }

    @Override
    public String getFrequency() {
        return frequency;
    }

}
