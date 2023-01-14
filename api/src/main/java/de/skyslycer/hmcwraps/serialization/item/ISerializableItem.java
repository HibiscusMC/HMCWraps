package de.skyslycer.hmcwraps.serialization.item;

import de.skyslycer.hmcwraps.IHMCWraps;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface ISerializableItem {

    @NotNull ItemStack toItem(IHMCWraps plugin, Player player);

    String getId();

    int getModelId();

    String getName();

    String getName(Player player);

    @Nullable
    List<String> getLore();

    @Nullable
    List<String> getLore(Player player);

    @Nullable
    List<String> getFlags();

    @Nullable
    Map<String, Integer> getEnchantments();

    @Nullable
    Integer getAmount();

    @Nullable
    Boolean isGlow();

    @Nullable
    Color getColor();

}
