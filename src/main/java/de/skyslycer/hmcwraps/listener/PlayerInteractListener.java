package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.gui.GuiBuilder;
import de.skyslycer.hmcwraps.permission.PermissionHelper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    private final HMCWraps plugin;

    public PlayerInteractListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }

        PermissionHelper.loopThroughInventory(plugin, event.getPlayer());

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK
                || plugin.getCollection().getItems(event.getItem().getType()).isEmpty() || !event.getPlayer().isSneaking()
                || !plugin.getConfiguration().isOpenShortcut()) {
            return;
        }
        event.setCancelled(true);
        GuiBuilder.open(plugin, event.getPlayer(), event.getItem(), event.getHand());
    }

}
