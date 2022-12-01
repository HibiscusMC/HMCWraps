package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.serialization.IWrap;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface IWrapper {

    /**
     * Get the player UUID the wrap belongs to.
     *
     * @param item The item
     * @return The player UUID
     */
    @Nullable
    UUID getOwningPlayer(ItemStack item);

    /**
     * If the provided player is owning the wrap on the item.
     *
     * @param item The item
     * @param player The wrap
     * @return If the wrap on the item belongs to the player
     */
    boolean isOwningPlayer(ItemStack item, Player player);

    /**
     * Set the owning player.
     *
     * @param item The item to set the owning player
     * @param uuid The UUID to set
     * @return The changed item
     */
    ItemStack setOwningPlayer(ItemStack item, UUID uuid);

    /**
     * Get the wrap on an item.
     *
     * @param item The item
     * @return The wrap
     */
    @Nullable
    IWrap getWrap(ItemStack item);

    /**
     * Wrap an item. If giveBack is true, the item is currently physically wrapped and the physical wrap was configured to give it back, it will also
     * give the player the wrapper of the old wrap.
     *
     * @param modelId The model id of the wrap
     * @param wrapId The wrap id
     * @param target The item to apply the wrap to
     * @param physical If the wrap was added physically
     * @param player The player
     * @param giveBack If the player should get his physical wrapper back
     * @param unwrap If the action is an unwrap and if so, won't change the original model id
     * @return The newly wrapped item
     */
    ItemStack setWrap(Integer modelId, String wrapId, ItemStack target, boolean physical, Player player, boolean giveBack, boolean unwrap);

    /**
     * The same as @see IWrapper#setWrap(Integer modelId, String wrapId, ItemStack target, boolean physical, Player player, boolean giveBack, boolean unwrap) the original method,
     * except that the last parameter (unwrap) is set to false. Good for everything not including an unwrap.
     *
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
     * Remove a wrap. If giveBack is true, the item is currently physically wrapped and the physical wrap was configured to give it back, it will also
     * give the player the wrapper of the old wrap.
     *
     * @param itemStack The item to remove the wrap from
     * @param player The player
     * @param giveBack If the player should get his physical wrapper back
     * @return The newly unwrapped item
     */
    ItemStack removeWrap(ItemStack itemStack, Player player, boolean giveBack);

    /**
     * Check if the item is an unwrapper.
     *
     * @param item The item
     * @return If the item is an unwrapper
     */
    boolean isPhysicalUnwrapper(ItemStack item);

    /**
     * Set the item to be a physical unwrapper.
     *
     * @param item The item to edit
     * @return The new unwrapper
     */
    ItemStack setPhysicalUnwrapper(ItemStack item);

    /**
     * Get the ID from a wrapper.
     *
     * @param item The item
     * @return THe wrapper ID
     */
    @Nullable
    String getPhysicalWrapper(ItemStack item);

    /**
     * Set the item to be a physical wrapper.
     *
     * @param item The item
     * @param wrapId The wrap the wrapper should give other items
     * @return The new wrapper
     */
    ItemStack setPhysicalWrapper(ItemStack item, String wrapId);

    /**
     * Get the saved model id of the item.
     * If the item doesn't have a saved model id, or it's disabled, it will use the default model id.
     * If that is disabled, or it doesn't exist, it will return -1;
     *
     * @param item The item
     * @return The model id
     */
    int getOriginalModelId(ItemStack item);

    /**
     * Set the saved model id of the item.
     *
     * @param item The item
     * @param originalModelid The model id
     * @return The changed item
     */
    ItemStack setOriginalModelId(ItemStack item, int originalModelid);

    /**
     * Set if the wrap was applied physically.
     *
     * @param item The item
     * @param physical If the wrap was applied physically
     * @return The changed item
     */
    ItemStack setPhysical(ItemStack item, boolean physical);

    /**
     * Check if the wrap was applied physically.
     *
     * @param item The item
     * @return If the wrap was apllied physically
     */
    boolean isPhysical(ItemStack item);

}
