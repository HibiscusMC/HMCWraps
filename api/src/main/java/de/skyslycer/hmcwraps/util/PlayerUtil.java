package de.skyslycer.hmcwraps.util;

import de.skyslycer.hmcwraps.HMCWraps;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerUtil {

    /**
     * Give a player an item and drop what doesn't fit.
     *
     * @param player The player
     * @param item   The item to give
     */
    public static void give(Player player, ItemStack item) {
        var drops = player.getInventory().addItem(item);
        drops.values().forEach(left -> player.getLocation().getWorld().dropItemNaturally(player.getLocation(), left));
    }

    /**
     * Get the block a player is looking at. Max distance is 2 blocks and min distance is 0 blocks.
     *
     * @param player The player
     * @return The block the player is looking at
     */
    public static Location getLookBlock(Player player) {
        var twoBlocks = fixLocation(player.getEyeLocation().add(player.getLocation().getDirection().clone().multiply(2)).subtract(0, 0.5, 0), player);
        var oneBlock = fixLocation(player.getEyeLocation().add(player.getLocation().getDirection().clone()).subtract(0, 0.5, 0), player);
        if (oneBlock.getWorld().getBlockAt(oneBlock).getType().isAir()) {
            if (twoBlocks.getWorld().getBlockAt(twoBlocks).getType().isAir()) {
                return twoBlocks;
            }
            return oneBlock;
        }
        return fixLocation(player.getLocation(), player);
    }

    /**
     * Get the block location on the opposite side of the player
     *
     * @param player The player
     * @return The opposite location
     */
    public static Location getOpposite(Player player) {
        var facing = player.getFacing().getOppositeFace();
        var location = player.getLocation().clone();
        location.add(facing.getModX(), facing.getModY(), facing.getModZ());
        var block = player.getLocation().getWorld().getBlockAt(location);
        return block.getLocation();
    }

    /**
     * Sets the Y-coordinate of the location to 1 higher than the players Y-level
     *
     * @param location The location to edit
     * @param player   The player
     * @return The changed location
     */
    private static Location fixLocation(Location location, Player player) {
        location.setY(player.getLocation().getY() + 1);
        return location;
    }

    /**
     * Shortcut for the implementation of the treat-as-no-item mechanic. Quickly filters out the specified materials.
     * Used as it's much shorter than the inline version.
     *
     * @param item The item to filter
     * @param plugin The plugin instance to access the configuration
     * @return The filtered item (if the material is contained in the list, returns null)
     */
    public static ItemStack filterNoItem(ItemStack item, HMCWraps plugin) {
        if (item == null || item.getType().isAir()) return null;
        var excluded = plugin.getConfiguration().getInventory().getTreatAsNoItem();
        if (excluded == null) return item;
        if (excluded.contains(item.getType().toString())) return null;
        return item;
    }

}
