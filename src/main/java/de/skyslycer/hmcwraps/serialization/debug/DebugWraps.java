package de.skyslycer.hmcwraps.serialization.debug;

import java.util.List;
import java.util.Map;

public class DebugWraps implements Debuggable {

    private final Map<String, List<String>> collections;
    private final Map<String, String> wraps;

    public DebugWraps(Map<String, List<String>> collections, Map<String, String> wraps) {
        this.collections = collections;
        this.wraps = wraps;
    }

}
