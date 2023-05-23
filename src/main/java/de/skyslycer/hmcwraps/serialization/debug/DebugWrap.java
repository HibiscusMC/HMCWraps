package de.skyslycer.hmcwraps.serialization.debug;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class DebugWrap implements Debuggable {

    private final String collection;
    private final List<Material> materials;
    private final Wrap wrap;

    public DebugWrap(Wrap wrap, String collection, List<Material> materials) {
        this.collection = collection;
        this.materials = materials;
        this.wrap = wrap;
    }

}
