package de.skyslycer.hmcwraps.serialization.debug;

import de.skyslycer.hmcwraps.serialization.IWrapValues;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;

import java.util.UUID;

public class DebugItemData implements Debuggable {

    private boolean physical;
    private String wrap;
    private UUID owningPlayer;
    private boolean physicalUnwrapper;
    private String physicalWrapper;
    private Wrap.WrapValues wrapValues;

    public DebugItemData(boolean physical, String wrap, UUID owningPlayer, boolean physicalUnwrapper, String physicalWrapper, IWrapValues wrapValues) {
        this.physical = physical;
        this.wrap = wrap;
        this.owningPlayer = owningPlayer;
        this.physicalUnwrapper = physicalUnwrapper;
        this.physicalWrapper = physicalWrapper;
        this.wrapValues = (Wrap.WrapValues) wrapValues;
    }

}
