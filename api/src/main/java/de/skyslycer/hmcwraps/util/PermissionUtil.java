package de.skyslycer.hmcwraps.util;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PermissionUtil {

    /**
     * Check if a player has the permission to use a wrap on an item based on the configuration.
     *
     * @param plugin The plugin
     * @param wrap   The wrap
     * @param item   The item stack
     * @param player The player
     * @return If the player has permission to use the wrap on the item
     */
    static boolean hasPermission(HMCWraps plugin, Wrap wrap, ItemStack item, Player player) {
        var wrapper = plugin.getWrapper();
        if (wrapper.isPhysical(item) && plugin.getConfiguration().getPermissions().isCheckPermissionPhysical() && !wrap.hasPermission(player)
                && !wrapper.isOwningPlayer(item, player)) {
            return false;
        }
        return wrapper.isPhysical(item) || !plugin.getConfiguration().getPermissions().isCheckPermissionVirtual() || wrap.hasPermission(player)
                || wrapper.isOwningPlayer(item, player);
    }

    /**
     * Check if the player has permission for the wrap on the item.
     *
     * @param plugin The plugin
     * @param item   The item
     * @param player The player
     * @return If the player doesn't have the required permission, it returns the item but unwrapped, while it returns null when the player has
     * permission
     */
    @Nullable
    public static ItemStack hasPermission(HMCWraps plugin, ItemStack item, Player player) {
        if (item.getType() == Material.AIR) {
            return null;
        }
        var wrap = plugin.getWrapper().getWrap(item);
        if (wrap == null) {
            return null;
        }
        if (!hasPermission(plugin, wrap, item, player)) {
            plugin.getMessageHandler().send(player, Messages.NO_PERMISSION_FOR_WRAP);
            return plugin.getWrapper().removeWrap(item, player, plugin.getConfiguration().getPermissions().isPermissionPhysical());
        }
        return null;
    }

    /**
     * Loops through a players inventory and unwraps items the player doesn't have access to.
     *
     * @param plugin The plugin
     * @param player The player
     */
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

    /**
     * Apply favorite wraps to all possible items.
     *
     * @param plugin The plugin
     * @param player The player
     */
    public static void applyFavorites(HMCWraps plugin, Player player, ItemStack item) {
        if (!plugin.getConfiguration().getFavorites().isEnabled()) {
            return;
        }
        for (int i = 0; i < player.getInventory().getSize() - 1; i++) {
            if (!Objects.equals(player.getInventory().getItem(i), item)) {
                continue;
            }
            var newItem = hasPermission(plugin, item, player);
            if (newItem != null || plugin.getWrapper().getWrap(item) == null) {
                int finalI = i;
                plugin.getCollectionHelper().getItems(item.getType()).forEach(it -> it.getWraps()
                        .values().stream().filter(wrap -> plugin.getWrapper().isValidModelId(item, wrap))
                        .filter(wrap -> wrap.hasPermission(player) && plugin.getFavoriteWrapStorage().get(player).contains(wrap)).findFirst().ifPresent(wrap ->
                                player.getInventory().setItem(finalI, plugin.getWrapper().setWrap(wrap, item, false, player, true))));
            }
            break;
        }

    }

    /**
     * Checks if a sender has any of the given permissions.
     *
     * @param sender      The sender to check on
     * @param permissions The permissions to check for
     * @return If the sender has any permissions
     */
    public static boolean hasAnyPermission(CommandSender sender, String... permissions) {
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

}
