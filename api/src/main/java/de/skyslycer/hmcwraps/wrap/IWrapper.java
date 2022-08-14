package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.serialization.IWrap;
import java.util.UUID;
import javax.annotation.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface IWrapper {

    /**
     * Check if the wrap was applied physically.
     * @param item The item
     * @return If the wrap was apllied physically
     */
    boolean isPhysical(ItemStack item);

    /**
     * Get the player UUID the wrap belongs to.
     * @param item The item
     * @return The player UUID
     */
    @Nullable
    UUID getOwningPlayer(ItemStack item);

    /**
     * Set the owning player.
     * @param item The item to set the owning player
     * @param uuid The UUID to set
     * @return The changed item
     */
    ItemStack setOwningPlayer(ItemStack item, UUID uuid);

    /**
     * If the provided player is owning the wrap on the item.
     * @param item The item
     * @param player The wrap
     * @return If the wrap on the item belongs to the player
     */
    boolean isOwningPlayer(ItemStack item, Player player);

    /**
     * Get the wrap on an item.
     * @param item The item
     * @return The wrap
     */
    @Nullable
    IWrap getWrap(ItemStack item);

    /**
     * Wrap an item. If giveBack is true, the item is currently physically wrapped and the physical
     * wrap was configured to give it back, it will also give the player the wrapper of the old wrap.
     * @param modelId The model id of the wrap
     * @param wrapId The wrap id
     * @param target The item to apply the wrap to
     * @param physical If the wrap was added physically
     * @param player The player
     * @param giveBack If the player should get his physical wrapper back
     * @return The newly wrapped item
     */
    ItemStack setWrap(Integer modelId, String wrapId, ItemStack target, boolean physical, Player player, boolean giveBack);

    /**
     * Remove a wrap. If giveBack is true, the item is currently physically wrapped and the physical
     * wrap was configured to give it back, it will also give the player the wrapper of the old wrap.
     * @param itemStack The item to remove the wrap from
     * @param player The player
     * @param giveBack If the player should get his physical wrapper back
     * @return The newly unwrapped item
     */
    ItemStack removeWrap(ItemStack itemStack, Player player, boolean giveBack);

    /**
     * Set the item to be a physical unwrapper.
     * @param item The item to edit
     * @return The new unwrapper
     */
    ItemStack setUnwrapper(ItemStack item);

    /**
     * Set the item to be a physical wrapper.
     * @param item The item
     * @param wrapId The wrap the wrapper should give other items
     * @return The new wrapper
     */
    ItemStack setWrapper(ItemStack item, String wrapId);

    /**
     * Check if the item is an unwrapper.
     * @param item The item
     * @return If the item is an unwrapper
     */
    boolean isUnwrapper(ItemStack item);

    /**
     * Get the ID from a wrapper.
     * @param item The item
     * @return THe wrapper ID
     */
    @Nullable
    String getWrapper(ItemStack item);

}
