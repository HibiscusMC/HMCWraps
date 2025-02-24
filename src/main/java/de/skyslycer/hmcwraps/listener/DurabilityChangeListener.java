package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
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
        if (updateDurability(event.getItem(), -event.getDamage())) {
            event.setDamage(0);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemMend(PlayerItemMendEvent event) {
        if (updateDurability(event.getItem(), event.getRepairAmount())) {
            event.setRepairAmount(0);
        }
    }

    private boolean updateDurability(ItemStack item, int changed) {
        var durability = plugin.getWrapper().getModifiers().armorImitation().getFakeDurability(item);
        var maxDurability = plugin.getWrapper().getModifiers().armorImitation().getFakeMaxDurability(item);
        if (plugin.getWrapper().getWrap(item) == null || plugin.getWrapper().getModifiers().armorImitation().getFakeDurability(item) == -1 || durability == -1) {
            return false;
        }
        var newDurability = Math.min(durability + changed, maxDurability);
        var modelDurability = ((double) newDurability / maxDurability) * item.getType().getMaxDurability();
        if (modelDurability == 0 && newDurability > 0) {
            modelDurability = 1;
        }
        plugin.getWrapper().getModifiers().armorImitation().setFakeDurability(item, newDurability);
        var meta = (Damageable) item.getItemMeta();
        meta.setDamage((int) (item.getType().getMaxDurability() - Math.round(modelDurability)));
        item.setItemMeta(meta);
        if (newDurability <= 0) {
            item.setAmount(0);
        }
        return true;
    }

}
