package de.skyslycer.hmcwraps;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;

public class PluginMetrics {

    private static final int PLUGIN_ID = 14554;

    private final HMCWraps plugin;
    private final Metrics metrics;

    public PluginMetrics(HMCWraps plugin) {
        this.plugin = plugin;
        this.metrics = new Metrics(plugin, PLUGIN_ID);
    }

    public void init() {
        metrics.addCustomChart(new SimplePie("wraps", () -> String.valueOf(plugin.getWrapAmount())));
        metrics.addCustomChart(new SimplePie("wrapfiles", () -> String.valueOf(plugin.getWrapFiles().size())));
    }

}
