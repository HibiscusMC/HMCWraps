package de.skyslycer.hmcwraps.integration.axtrade;

import com.artillexstudios.axauctions.api.events.AxAuctionsPreSellEvent;
import com.artillexstudios.axtrade.api.events.AxTradeCompleteEvent;
import de.skyslycer.hmcwraps.HMCWraps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class AxTradeCompleteListener implements Listener {

    private final HMCWraps plugin;

    public AxTradeCompleteListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreSell(AxTradeCompleteEvent event) {
        var items = new ArrayList<ItemStack>();
        items.addAll(event.getTrade().getPlayer1().getTradeGui().getItems(false));
        items.addAll(event.getTrade().getPlayer2().getTradeGui().getItems(false));
        items.forEach(stack -> {
            if (plugin.getConfiguration().getPluginIntegrations().getAuctionHouse().isBlacklisted(plugin, stack, true)) {
                event.setCancelled(true);
            }
        });
    }

}
