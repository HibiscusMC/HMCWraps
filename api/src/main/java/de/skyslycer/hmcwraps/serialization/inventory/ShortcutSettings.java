package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ShortcutSettings extends Toggleable {

    private List<String> exclude;

    public List<String> getExclude() {
        return exclude;
    }

}
