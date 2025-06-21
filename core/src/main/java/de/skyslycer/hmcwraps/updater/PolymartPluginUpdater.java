package de.skyslycer.hmcwraps.updater;

public class PolymartPluginUpdater extends PluginUpdater {

    /**
     * Create a new Polymart plugin updater.
     *
     * @param pluginId The plugin id
     */
    public PolymartPluginUpdater(int pluginId) {
        super(pluginId, PluginPlatform.POLYMART);
    }

    @Override
    public String parse(String body) {
        return body;
    }

}
