package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    private final HMCWraps plugin;

    public InventoryClickListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != event.getWhoClicked().getInventory() || event.getCursor() == null
                || event.getCurrentItem() == null) {
            return;
        }

        var physical = event.getCursor().clone();
        var target = event.getCurrentItem().clone();
        var cursor = physical.clone();
        if (cursor.getAmount() != 1) {
            cursor.setAmount(cursor.getAmount() - 1);
        } else {
            cursor = null;
        }

        if (physical.isSimilar(plugin.getConfiguration().getUnwrapper().toItem(plugin))
                && plugin.getWrapper().getWrap(target) != null) {
            plugin.getWrapper().removeWrap(target, (Player) event.getWhoClicked());
            event.setCursor(cursor);
            event.setCancelled(true);
            return;
        }

        var wrappableItem = plugin.getConfiguration().getItems().get(target.getType().toString());
        if (wrappableItem == null) {
            return;
        }
        var finalCursor = cursor;
        wrappableItem.getWraps().values().stream().filter(wrap -> wrap.getPhysical() != null)
                .filter(wrap -> physical.isSimilar(wrap.getPhysical().toItem(plugin))).findFirst().ifPresent(wrap -> {
                    plugin.getWrapper().setWrap(plugin.getModellIdFromHook(wrap.getId()), wrap.getUuid(), target, true,
                            (Player) event.getWhoClicked());
                    event.setCursor(finalCursor);
                    event.setCancelled(true);
                });
    }

}
