package de.skyslycer.hmcwraps.transformation;

import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class FileTransformations {

    private static final Pattern VERSION_PATTERN = Pattern.compile("config: (?<ver>\\d)");

    private int latest = -1;
    private final Map<Integer, UpdateMethod> updateMethods = new HashMap<>();

    @FunctionalInterface
    protected interface UpdateMethod {
        void update(Path path) throws IOException;
    }

    private int getConfigVersion(Path path) throws IOException {
        var config = Files.readString(path);
        var matcher = VERSION_PATTERN.matcher(config);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group("ver"));
        } else {
            return 0;
        }
    }

    public void updateToLatest(Path path) throws IOException {
        var currentVersion = getConfigVersion(path);
        while (currentVersion < latest) {
            var method = updateMethods.get(currentVersion);
            if (method != null) {
                method.update(path);
                currentVersion = getConfigVersion(path);
            } else {
                Bukkit.getLogger().severe("Could not find update method for config version " + currentVersion + "! Please report this to the developers!");
            }
        }
    }

    protected void addUpdateMethod(int version, UpdateMethod method) {
        updateMethods.put(version, method);
    }

    protected void setLatest(int latest) {
        this.latest = latest;
    }

}
