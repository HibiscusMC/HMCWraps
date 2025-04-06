package de.skyslycer.hmcwraps.updater;

public record CheckResult(String version, String url, PluginPlatform platform, boolean latest) {

    /**
     * Create a new check result.
     *
     * @param version  The latest version of the plugin.
     * @param url      The url to the resource
     * @param platform The platform of the resource
     * @param latest   If the plugin is the latest version
     */
    public CheckResult {
    }

    /**
     * Get the new version.
     *
     * @return The new version
     */
    @Override
    public String version() {
        return version;
    }

    /**
     * Get the plugin url.
     *
     * @return The plugin url
     */
    @Override
    public String url() {
        return url;
    }

    /**
     * Get the update platform.
     *
     * @return The update platform
     */
    @Override
    public PluginPlatform platform() {
        return platform;
    }

    /**
     * Check if the plugin is the latest version.
     *
     * @return If the plugin is the latest version
     */
    @Override
    public boolean latest() {
        return latest;
    }

}
