package de.skyslycer.hmcwraps.updater.version;

import java.util.regex.Pattern;

public class PluginVersion {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)+");

    private final int major;
    private final int minor;
    private final int patch;

    /**
     * Create a new plugin version.
     *
     * @param major The major
     * @param minor The minor
     * @param patch The patch
     */
    public PluginVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Check if this version is older than the other.
     *
     * @param other The version to compare
     * @return If this one is older
     */
    public boolean isOlderThan(PluginVersion other) {
        if (major < other.major) {
            return true;
        } else if (minor < other.minor && major <= other.major) {
            return true;
        } else return patch < other.patch && minor <= other.minor && major <= other.major;
    }

    /**
     * Get the matched version from a string.
     *
     * @param version The version string
     * @return The matched version or in case of no match null
     */
    public static PluginVersion fromString(String version) {
        var matcher = PATTERN.matcher(version);
        if (matcher.matches()) {
            return new PluginVersion(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)), Integer.parseInt(matcher.group(3)));
        }
        return null;
    }

}
