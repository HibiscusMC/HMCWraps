package de.skyslycer.hmcwraps.updater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.skyslycer.hmcwraps.updater.version.PluginVersion;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public abstract class PluginUpdater {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder().create();

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
    public CheckResult check(JavaPlugin plugin) {
        try {
            var request = CLIENT.send(HttpRequest.newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.72 Safari/537.36 Edg/90.0.818.42")
                    .uri(URI.create(String.format(platform().apiUrl(), pluginId())))
                    .build(), HttpResponse.BodyHandlers.ofInputStream());
            try (var body = request.body()) {
                var json = GSON.fromJson(new String(body.readAllBytes(), StandardCharsets.UTF_8), JsonObject.class);
                var version = parse(json);
                if (version != null && PluginVersion.fromString(plugin.getDescription().getVersion()).isOlderThan(PluginVersion.fromString(version))) {
                    return new CheckResult(version, String.format(platform().url(), pluginId()), platform());
                }
            }
        } catch (IOException | InterruptedException | NullPointerException ignored) {
        }
        return null;
    }

    /**
     * Parse the returned JsonObject into the latest version.
     *
     * @param object The json the request returned
     * @return A usable version
     */
    public abstract String parse(JsonObject object);

}
