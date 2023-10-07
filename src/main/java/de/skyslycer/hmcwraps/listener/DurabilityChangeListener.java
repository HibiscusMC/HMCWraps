package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class DurabilityChangeListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public DurabilityChangeListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemDamage(PlayerItemDamageEvent event) {
        var item = event.getItem();
        updateDurability(item, -event.getDamage());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemMend(PlayerItemMendEvent event) {
        var item = event.getItem();
        updateDurability(item, event.getRepairAmount());
    }

    private void updateDurability(ItemStack item, int changed) {
        var originalData = plugin.getWrapper().getOriginalData(item);
        var durability = plugin.getWrapper().getFakeDurability(item);
        if (plugin.getWrapper().getWrap(item) == null || originalData == null || originalData.material().isBlank() || durability == -1) {
            return;
        }
        var material = Material.valueOf(originalData.material());
        var maxDurability = material.getMaxDurability();
        var newDurability = Math.min(durability + changed, maxDurability);
        var modelDurability = ((double) newDurability / maxDurability) * item.getType().getMaxDurability();
        if (modelDurability == 0 && newDurability > 0) {
            modelDurability = 1;
        }
        plugin.getWrapper().setFakeDurability(item, newDurability);
        var meta = (Damageable) item.getItemMeta();
        meta.setDamage(item.getType().getMaxDurability() - (int) modelDurability);
        item.setItemMeta(meta);
        if (newDurability <= 0) {
            item.setAmount(0);
        }
    }

}
