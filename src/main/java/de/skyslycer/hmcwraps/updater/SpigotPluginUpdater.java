package de.skyslycer.hmcwraps.updater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class SpigotPluginUpdater extends PluginUpdater {

    private static final Gson GSON = new GsonBuilder().create();

    /**
     * Create a new Spigot plugin updater.
     *
     * @param pluginId The plugin id
     */
    public SpigotPluginUpdater(int pluginId) {
        super(pluginId, PluginPlatform.SPIGOT_MC);
    }

    @Override
    public String parse(String body) {
        var object = GSON.fromJson(body, JsonObject.class);
        return object.getAsJsonPrimitive("name").getAsString();
    }

}
