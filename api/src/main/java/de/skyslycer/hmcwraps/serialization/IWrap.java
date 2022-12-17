package de.skyslycer.hmcwraps.serialization;

import de.skyslycer.hmcwraps.serialization.item.ISerializableItem;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;

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
    Color getColor();

    @Nullable
    String getLockedName();

    @Nullable
    List<String> getLockedLore();

    @Nullable
    HashMap<String, HashMap<String, List<String>>> getActions();

}
