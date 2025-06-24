package de.skyslycer.hmcwraps.integration;

public interface IntegrationHandler {

    /**
     * Loads the integration.
     * This method should be called when the plugin is enabled or reloaded.
     */
    void load();

    /**
     * Unloads the integration.
     * This method should be called when the plugin is disabled or reloaded.
     */
    void unload();

}
