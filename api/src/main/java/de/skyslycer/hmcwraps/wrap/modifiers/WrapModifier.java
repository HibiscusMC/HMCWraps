package de.skyslycer.hmcwraps.wrap.modifiers;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public interface WrapModifier {

    String SEPARATOR = ";!;";

    void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player);

}
