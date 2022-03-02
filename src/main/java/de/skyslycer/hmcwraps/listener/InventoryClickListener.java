package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener {

    private final HMCWraps plugin;

    public InventoryClickListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != event.getWhoClicked().getInventory() || event.getCursor() == null
                || event.getCurrentItem() == null || event.getCurrentItem().getItemMeta() == null) {
            return;
        }

        var player = (Player) event.getWhoClicked();
        var physical = event.getCursor().clone();
        var target = event.getCurrentItem().clone();
        var cursor = physical.clone();
        if (cursor.getAmount() != 1) {
            cursor.setAmount(cursor.getAmount() - 1);
        } else {
            cursor = null;
        }

        if (physical.isSimilar(plugin.getConfiguration().getUnwrapper().toItem(plugin, player))
                && plugin.getWrapper().getWrap(target) != null) {
            event.setCurrentItem(plugin.getWrapper().removeWrap(target, player));
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
                .filter(wrap -> physical.isSimilar(wrap.getPhysical().toItem(plugin, player))).findFirst().ifPresent(wrap -> {
                    event.setCurrentItem(plugin.getWrapper().setWrap(wrap.getModelId(), wrap.getUuid(), target, true,
                            (Player) event.getWhoClicked()));
                    event.setCursor(finalCursor);
                    event.setCancelled(true);
                });
    }

}
