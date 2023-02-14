package de.skyslycer.hmcwraps.serialization.wrap.range;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ValueRangeSettings<T> {

    private @Nullable List<T> include;
    private @Nullable List<T> exclude;

    public ValueRangeSettings(@Nullable List<T> include, @Nullable List<T> exclude) {
        this.include = include;
        this.exclude = exclude;
    }

    public ValueRangeSettings() {
    }

    @Nullable
    public List<T> getInclude() {
        return include;
    }

    @Nullable
    public List<T> getExclude() {
        return exclude;
    }

}
