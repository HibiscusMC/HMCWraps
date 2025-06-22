package de.skyslycer.hmcwraps.metrics;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class PluginMetrics {

    private static final int PLUGIN_ID = 14554;

    private final HMCWrapsPlugin plugin;
    private final Metrics metrics;

    public PluginMetrics(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
        this.metrics = new Metrics(plugin, PLUGIN_ID);
    }

    public void init() {
        metrics.addCustomChart(new SimplePie("wraps", () -> String.valueOf(plugin.getWrapsLoader().getWraps().size())));
        metrics.addCustomChart(new SimplePie("wrapfiles", () -> String.valueOf(plugin.getWrapsLoader().getWrapFileCount())));
        metrics.addCustomChart(new SimplePie("collectionfiles", () -> String.valueOf(plugin.getWrapsLoader().getCollectionFileCount())));
    }

}
