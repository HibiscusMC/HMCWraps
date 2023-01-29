package de.skyslycer.hmcwraps.updater;

public enum PluginPlatform {

    /**
     * The base for plugins on SpigotMC.
     */
    SPIGOT_MC("SpigotMC", "https://www.spigotmc.org/resources/%d", "https://api.spiget.org/v2/resources/%d/versions/latest"),
    /**
     * The base for plugins on Polymart.
     */
    POLYMART("Polymart", "https://polymart.org/resource/%d", "https://api.polymart.org/v1/getResourceInfo?resource_id=%d");

    private final String name;
    private final String url;
    private final String apiUrl;

    /**
     * Create a new plugin platform.
     *
     * @param name The name of the platform
     * @param url The URL of the platform
     * @param apiUrl The API URL of the platform
     */
    PluginPlatform(String name, String url, String apiUrl) {
        this.name = name;
        this.url = url;
        this.apiUrl = apiUrl;
    }

    /**
     * Get the platform name.
     *
     * @return The platform name
     */
    public String platformName() {
        return name;
    }

    /**
     * Get the plugin url.
     *
     * @return The plugin url
     */
    public String url() {
        return url;
    }

    /**
     * Get the API url.
     *
     * @return The API url
     */
    public String apiUrl() {
        return apiUrl;
    }

}
