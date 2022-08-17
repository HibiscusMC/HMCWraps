package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public interface IWrap extends ISerializableItem {

    @Nullable
    String getPermission();

    String getUuid();

    @Nullable
    IPhysicalWrap getPhysical();

    Boolean isPreview();

    /**
     * If the sender has permission to use this wrap.
     * @param sender The sender
     * @return If the sender has permission
     */
    boolean hasPermission(CommandSender sender);

}
