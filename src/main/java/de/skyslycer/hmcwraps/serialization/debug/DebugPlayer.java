package de.skyslycer.hmcwraps.serialization.debug;

import java.util.List;

public class DebugPlayer implements Debuggable {

    private final List<String> favorites;
    private final boolean filter;
    private final DebugItemData wrapInHand;

    public DebugPlayer(List<String> favorites, boolean filter, DebugItemData wrapInHand) {
        this.favorites = favorites;
        this.filter = filter;
        this.wrapInHand = wrapInHand;
    }

}
