package de.skyslycer.hmcwraps.serialization.debug;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;

import java.util.UUID;

public class DebugItemData implements Debuggable {

    private String material;
    private boolean physical;
    private String wrap;
    private UUID owningPlayer;
    private boolean physicalUnwrapper;
    private String physicalWrapper;
    private Wrap.WrapValues wrapValues;
    private int fakeDurability;
    private int fakeMaxDurability;
    private String nbt;

    public DebugItemData(String material, boolean physical, String wrap, UUID owningPlayer, boolean physicalUnwrapper,
                         String physicalWrapper, Wrap.WrapValues wrapValues, int fakeDurability, int fakeMaxDurability, String nbt) {
        this.material = material;
        this.physical = physical;
        this.wrap = wrap;
        this.owningPlayer = owningPlayer;
        this.physicalUnwrapper = physicalUnwrapper;
        this.physicalWrapper = physicalWrapper;
        this.wrapValues = wrapValues;
        this.fakeDurability = fakeDurability;
        this.fakeMaxDurability = fakeMaxDurability;
        this.nbt = nbt;
    }

}
