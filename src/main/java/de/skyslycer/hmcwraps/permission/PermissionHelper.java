package de.skyslycer.hmcwraps.permission;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.Wrap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PermissionHelper {

    public static boolean hasPermission(HMCWraps plugin, Wrap wrap, ItemStack item, Player player) {
        var wrapper = plugin.getWrapper();
        if (wrapper.isPhysical(item) && plugin.getConfiguration().getPermissionSettings().isCheckPermissionPhysical() && !wrap.hasPermission(player) && !wrapper.isOwningPlayer(item, player)) {
            return false;
        }
        return wrapper.isPhysical(item) || !plugin.getConfiguration().getPermissionSettings().isCheckPermissionVirtual() || wrap.hasPermission(player) || wrapper.isOwningPlayer(item, player);
    }

    public static ItemStack hasPermission(HMCWraps plugin, ItemStack item, Player player) {
        if (item.getType() == Material.AIR) {
            return null;
        }
        var wrap = plugin.getWrapper().getWrap(item);
        if (wrap == null) {
            return null;
        }
        if (!hasPermission(plugin, wrap, item, player)) {
            plugin.getHandler().send(player, Messages.NO_PERMISSION_FOR_WRAP);
            return plugin.getWrapper().removeWrap(item, player, plugin.getConfiguration().getPermissionSettings().isPermissionPhysical());
        }
        return null;
    }

    public static void loopThroughInventory(HMCWraps plugin, Player player) {
        for (int i = 0; i < player.getInventory().getContents().length - 1; i++) {
            var item = player.getInventory().getItem(i);
            if (item == null) {
                continue;
            }
            var newItem = hasPermission(plugin, item, player);
            if (newItem != null) {
                player.getInventory().setItem(i, newItem);
            }
        }
    }

}
