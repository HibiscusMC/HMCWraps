package de.skyslycer.hmcwraps.updater;

import com.google.gson.JsonObject;

public class SpigotPluginUpdater extends PluginUpdater {

    /**
     * Create a new Spigot plugin updater.
     *
     * @param pluginId The plugin id
     */
    public SpigotPluginUpdater(int pluginId) {
        super(pluginId, PluginPlatform.SPIGOT_MC);
    }

    @Override
    public String parse(JsonObject object) {
        return object.getAsJsonPrimitive("name").getAsString();
    }

}
