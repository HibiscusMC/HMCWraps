package de.skyslycer.hmcwraps.serialization.item;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private @Nullable String color;

    public SerializableItem(String id, String name, @Nullable Boolean glow, @Nullable List<String> lore, @Nullable List<String> flags,
                            @Nullable Integer modelId, @Nullable Map<String, Integer> enchantments, @Nullable Integer amount, @Nullable String color) {
        this.id = id;
        this.name = name;
        this.glow = glow;
        this.lore = lore;
        this.flags = flags;
        this.modelId = modelId;
        this.enchantments = enchantments;
        this.amount = amount;
        this.color = color;
    }

    public SerializableItem() {
    }

    @NotNull
    public ItemStack toItem(HMCWraps plugin, Player player) {
        ItemStack origin = plugin.getHookAccessor().getItemFromHook(getId());
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

        if (getColor() != null) {
            builder.color(getColor());
        }
        if (getLore() != null) {
            builder.lore(player == null ? getLore().stream().map(StringUtil::parseComponent).toList()
                    : getLore().stream().map(string -> StringUtil.parseComponent(player, string)).toList());
        }
        if (getFlags() != null) {
            List<ItemFlag> parsed = Arrays.asList(ItemFlag.values());
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
            modelId = ((HMCWraps) Bukkit.getPluginManager().getPlugin("HMCWraps")).getHookAccessor().getModelIdFromHook(getId());
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

    @Nullable
    public Color getColor() {
        if (color == null) {
            return ((HMCWraps) Bukkit.getPluginManager().getPlugin("HMCWraps")).getHookAccessor().getColorFromHook(getId());
        }
        return StringUtil.colorFromString(color);
    }

}
