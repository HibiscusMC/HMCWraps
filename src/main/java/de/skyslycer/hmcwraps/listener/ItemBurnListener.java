package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class ItemBurnListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public ItemBurnListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onItemBurn(EntityCombustEvent event) {
        if (!(event.getEntity() instanceof Item item)) {
            return;
        }
        var originalData = plugin.getWrapper().getOriginalData(item.getItemStack());
        if (plugin.getWrapper().getWrap(item.getItemStack()) != null && originalData != null && !originalData.material().isBlank() && originalData.material().contains("NETHERITE")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityBurn(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Item item)) {
            return;
        }
        if (event.getCause() != EntityDamageEvent.DamageCause.FIRE && event.getCause() != EntityDamageEvent.DamageCause.LAVA) {
            return;
        }
        var originalData = plugin.getWrapper().getOriginalData(item.getItemStack());
        if (plugin.getWrapper().getWrap(item.getItemStack()) != null && originalData != null && !originalData.material().isBlank() && originalData.material().contains("NETHERITE")) {
            event.setCancelled(true);
        }
    }

}
