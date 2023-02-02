package de.skyslycer.hmcwraps.serialization.filter;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

public class FilterSettings extends Toggleable implements IFilterSettings {

    @Setting("default")
    private boolean defaultFilter;

    public FilterSettings(boolean enabled, boolean defaultFilter) {
        super(enabled);
        this.defaultFilter = defaultFilter;
    }

    public FilterSettings(boolean defaultFilter) {
        this.defaultFilter = defaultFilter;
    }

    @Override
    public boolean getDefault() {
        return defaultFilter;
    }

}
