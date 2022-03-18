package de.skyslycer.hmcwraps.serialization.item;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.EnumUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class SerializableItem {

    private String id;
    private String name;
    private @Nullable Boolean glow;
    private @Nullable List<String> lore;
    private @Nullable List<String> flags;
    private @Nullable Integer modelId;
    private @Nullable Map<String, Integer> enchantments;
    private @Nullable Integer amount;

    @NotNull
    public ItemStack toItem(HMCWraps plugin, Player player) {
        ItemStack origin = plugin.getItemFromHook(getId());
        if (origin == null) {
            origin = new ItemStack(Material.STRUCTURE_VOID);
        }
        if (origin.getType() == Material.AIR) {
            return origin;
        }

        ItemBuilder builder = ItemBuilder.from(origin);
        builder.name(player == null ? StringUtil.parseComponent(getName()) : StringUtil.parseComponent(player, getName()))
                .amount(getAmount() == null ? 1 : getAmount())
                .model(getModelId());

        if (getLore() != null) {
            builder.lore(getLore().stream()
                    .map(it -> player == null ? StringUtil.parseComponent(it) : StringUtil.parseComponent(player, it))
                    .collect(Collectors.toList()));
        }
        if (getFlags() != null) {
            List<ItemFlag> parsed = EnumUtil.getAllPossibilities(getFlags(), ItemFlag.class);
            builder.flags(parsed.toArray(ItemFlag[]::new));
        }
        if (getEnchantments() != null) {
            getEnchantments().forEach((name, level) -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name));
                if (enchantment != null) {
                    builder.enchant(enchantment, level);
                }
            });
        }
        if (Boolean.TRUE.equals(isGlow())) {
            builder.glow();
        }
        return builder.build();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public List<String> getLore() {
        return lore;
    }

    @Nullable
    public List<String> getFlags() {
        return flags;
    }

    public int getModelId() {
        if (modelId == null) {
            modelId = HMCWraps.getPlugin(HMCWraps.class).getModelIdFromHook(getId());
        }
        return modelId;
    }

    @Nullable
    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    @Nullable
    public Integer getAmount() {
        return amount;
    }

    @Nullable
    public Boolean isGlow() {
        return glow;
    }

}
