package de.skyslycer.hmcwraps.integration.nexo;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.IntegrationHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

public class NexoHandler implements IntegrationHandler {

    private final HMCWraps plugin;

    private NexoItemsLoadedListener listener = null;

    public NexoHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        listener = new NexoItemsLoadedListener(plugin);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void unload() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
        }
    }

}
