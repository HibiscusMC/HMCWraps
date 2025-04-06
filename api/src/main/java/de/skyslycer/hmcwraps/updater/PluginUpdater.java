package de.skyslycer.hmcwraps.updater;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.updater.version.PluginVersion;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public abstract class PluginUpdater {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private final int pluginId;
    private final PluginPlatform platform;

    /**
     * Create a new plugin updater.
     *
     * @param pluginId The plugin id
     * @param platform The platform
     */
    protected PluginUpdater(int pluginId, PluginPlatform platform) {
        this.pluginId = pluginId;
        this.platform = platform;
    }

    /**
     * Get the plugin id for the platform.
     *
     * @return The platform plugin id
     */
    public int pluginId() {
        return pluginId;
    }

    /**
     * Get the updater platform.
     *
     * @return The platform
     */
    public PluginPlatform platform() {
        return platform;
    }

    /**
     * Check if there is a new update.
     *
     * @param plugin The plugin
     * @return The new version or, if no new version is available, null
     */
    public CheckResult check(HMCWraps plugin) {
        try {
            var request = CLIENT.send(HttpRequest.newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.72 Safari/537.36 Edg/90.0.818.42")
                    .uri(URI.create(String.format(platform().apiUrl(), pluginId())))
                    .build(), HttpResponse.BodyHandlers.ofInputStream());
            try (var body = request.body()) {
                var version = parse(new String(body.readAllBytes(), StandardCharsets.UTF_8).trim());
                var pluginVersion = plugin.getDescription().getVersion();
                if (pluginVersion.split("-").length > 1) {
                    pluginVersion = pluginVersion.split("-")[0];
                }
                if (version != null) {
                    if (PluginVersion.fromString(pluginVersion).isOlderThan(PluginVersion.fromString(version))) {
                        return new CheckResult(version, String.format(platform().url(), pluginId()), platform(), false);
                    } else {
                        return new CheckResult(version, String.format(platform().url(), pluginId()), platform(), true);
                    }
                }
            }
        } catch (Exception exception) {
            plugin.logSevere("Failed to check for updates. Report this error to the developers, but you can continue using this plugin without restrictions. Error: ", exception);
        }
        return null;
    }

    /**
     * Parse the returned body into the latest version.
     *
     * @param body The body of the response
     * @return A usable version
     */
    public abstract String parse(String body);

}
