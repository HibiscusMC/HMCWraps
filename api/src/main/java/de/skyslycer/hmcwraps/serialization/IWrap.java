package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.IHMCWraps;
import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface IWrap extends ISerializableItem {

    Optional<String> getPermission();

    String getUuid();

    Optional<IPhysicalWrap> getPhysical();

    Boolean isPreview();

    /**
     * If the sender has permission to use this wrap.
     *
     * @param sender The sender
     * @return If the sender has permission
     */
    boolean hasPermission(CommandSender sender);

    @Nullable
    String getLockedName();

    @Nullable
    List<String> getLockedLore();

    @Nullable
    HashMap<String, HashMap<String, List<String>>> getActions();

    List<Integer> getModelIdInclude();

    List<Integer> getModelIdExclude();

    @Nullable
    ISerializableItem getLockedItem();

    @Nullable
    String getWrapName();

    /**
     * Get the item for the player according to its permissions.
     * If the player doesn't have the permissions, the locked item will be returned.
     * If there is no locked item, the item with applied locked lore and locked name will be returned.
     * If that isn't configured, it will just return the original item.
     *
     * @param plugin The plugin instance
     * @param player The player
     * @return The item for the player
     */
    ItemStack toPermissionItem(IHMCWraps plugin, Player player);

}
