package de.skyslycer.hmcwraps.circle;

import de.skyslycer.hmcwraps.serialization.CircleIdentity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CircleManager {

    private static final int POINTS = 720;

    private final Map<CircleIdentity, Set<Point>> locations = new HashMap<>();

    public void generateCircleLocations(CircleIdentity identity) {
        var set = new HashSet<Point>();
        for (int i = 0; i < POINTS; i++) {
            double angle = Math.toRadians(((double) i / POINTS) * 360d);
            set.add(Point.build(Math.cos(angle) * identity.getAmplitude(), Math.sin(angle) * identity.getAmplitude()));
        }
        locations.put(identity, set);
    }

    public Map<CircleIdentity, Set<Point>> getLocations() {
        return locations;
    }

    public void clear() {
        locations.clear();
    }

}
