package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import java.util.Objects;
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

        if (plugin.getWrapper().isUnwrapper(physical) && plugin.getWrapper().getWrap(target) != null) {
            event.setCurrentItem(plugin.getWrapper().removeWrap(target, player));
            event.setCursor(cursor);
            event.setCancelled(true);
            return;
        }

        var wrapId = plugin.getWrapper().getWrapper(physical);
        if (wrapId == null) {
            return;
        }

        var wrap = plugin.getWraps().get(wrapId);
        if (wrap == null) {
            return;
        }
        var finalCursor = cursor;
        if (wrap.getPhysical() != null && wrap.hasPermission(player)) {
            event.setCurrentItem(plugin.getWrapper().setWrap(wrap.getModelId(), wrap.getUuid(), target, true,
                    (Player) event.getWhoClicked()));
            event.setCursor(finalCursor);
            event.setCancelled(true);
        }
    }

}
