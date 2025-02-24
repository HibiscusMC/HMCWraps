package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.util.WrapNBTUtil;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class NBTModifier implements WrapModifier {

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (wrap != null) {
            if (wrap.getWrapNbt() != null) {
                WrapNBTUtil.wrap(item, StringUtil.replacePlaceholders(player, wrap.getWrapNbt()));
            }
        } else {
            WrapNBTUtil.unwrap(item);
        }
    }

}
