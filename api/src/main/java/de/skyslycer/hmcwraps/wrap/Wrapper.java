package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Wrapper {

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
     * @param item   The item
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
    Wrap getWrap(ItemStack item);

    /**
     * Wrap an item. If giveBack is true, the item is currently physically wrapped and the physical wrap was configured to give it back, it will also
     * give the player the wrapper of the old wrap.
     *
     * @param wrap     The wrap to apply, if null, the item will be unwrapped
     * @param target   The item to apply the wrap to
     * @param physical If the wrap was added physically
     * @param player   The player
     * @param giveBack If the player should get his physical wrapper back
     * @return The newly wrapped item
     */
    ItemStack setWrap(Wrap wrap, ItemStack target, boolean physical, Player player, boolean giveBack);

    /**
     * Remove a wrap. If giveBack is true, the item is currently physically wrapped and the physical wrap was configured to give it back, it will also
     * give the player the wrapper of the old wrap.
     *
     * @param itemStack The item to remove the wrap from
     * @param player    The player
     * @param giveBack  If the player should get his physical wrapper back
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
     * @param wrap The wrap the wrapper should give other items
     * @return The new wrapper
     */
    ItemStack setPhysicalWrapper(ItemStack item, Wrap wrap);

    /**
     * Get the saved data of the item.
     *
     * @param item The item
     * @return The data
     */
    Wrap.WrapValues getOriginalData(ItemStack item);

    /**
     * Set the saved data of the item.
     *
     * @param wrapValues All values to set
     * @return The changed item
     */
    ItemStack setOriginalData(ItemStack item, Wrap.WrapValues wrapValues);

    /**
     * Set if the wrap was applied physically.
     *
     * @param item     The item
     * @param physical If the wrap was applied physically
     * @return The changed item
     */
    ItemStack setPhysical(ItemStack item, boolean physical);

    /**
     * Check if the wrap was applied physically.
     *
     * @param item The item
     * @return If the wrap was applied physically
     */
    boolean isPhysical(ItemStack item);

    /**
     * Check if the items model id is valid for the wrap.
     *
     * @param item The item
     * @param wrap The wrap
     * @return If the model id is valid
     */
    boolean isValid(ItemStack item, Wrap wrap);

}
