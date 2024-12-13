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
     * @return The wrap, null if the item isn't wrapped
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
     * @return The newly wrapped item
     */
    ItemStack setWrap(Wrap wrap, ItemStack target, boolean physical, Player player);

    /**
     * Remove a wrap. If giveBack is true, the item is currently physically wrapped and the physical wrap was configured to give it back, it will also
     * give the player the wrapper of the old wrap.
     *
     * @param itemStack The item to remove the wrap from
     * @param player    The player
     * @return The newly unwrapped item
     */
    ItemStack removeWrap(ItemStack itemStack, Player player);

    /**
     * Get the fake durability of the item.
     * If the item isn't changing durability, it will return -1.
     *
     * @param item The item
     * @return The fake durability
     */
    int getFakeDurability(ItemStack item);

    /**
     * Set the fake durability of the item.
     *
     * @param item The item
     * @param durability The durability to set
     */
    void setFakeDurability(ItemStack item, int durability);

    /**
     * Get the fake max durability of the item.
     * If the item isn't changing durability, it will return -1.
     *
     * @param item The item
     * @return The fake durability
     */
    int getFakeMaxDurability(ItemStack item);

    /**
     * Set the fake max durability of the item.
     *
     * @param item The item
     * @param durability The durability to set
     */
    void setFakeMaxDurability(ItemStack item, int durability);

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
     * Set the item to be using trims. This will prevent the item from being used in a smithing table.
     *
     * @param item The item
     * @param used If the item is using trims
     * @return The changed item
     */
    ItemStack setTrimsUsed(ItemStack item, boolean used);

    /**
     * Check if the item is using trims.
     *
     * @param item The item
     * @return If the item is using trims
     */
    boolean isTrimsUsed(ItemStack item);

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
     * Check if the original item had custom attributes instead of the default ones.
     *
     * @param item The item
     * @return If the item had custom attributes applied
     */
    boolean isCustomAttributes(ItemStack item);

    /**
     * Check if the items model id is valid for the wrap.
     *
     * @param item The item
     * @param wrap The wrap
     * @return If the model id is valid
     */
    boolean isValid(ItemStack item, Wrap wrap);

    /**
     * Check if the item is globally disabled to not be able to be wrapped.
     *
     * @param item The item to check
     * @return If the item is globally disabled
     */
    boolean isGloballyDisabled(ItemStack item);

}
