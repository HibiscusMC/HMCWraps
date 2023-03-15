package de.skyslycer.hmcwraps.serialization.filter;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class FilterSettings extends Toggleable {

    @Setting("default")
    private boolean defaultFilter;

    public boolean getDefault() {
        return defaultFilter;
    }

}
