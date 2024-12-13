package de.skyslycer.hmcwraps.serialization.item;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.StringUtil;
import de.skyslycer.hmcwraps.util.VersionUtil;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private @Nullable String nbt;
    private @Nullable Integer durability;
    private @Nullable String skullOwner;
    private @Nullable String skullTexture;
    private @Nullable String trim;
    private @Nullable String trimMaterial;
    private @Nullable String equippableSlot;
    private @Nullable String equippableModel;

    public SerializableItem(String id, String name, @Nullable Boolean glow, @Nullable List<String> lore, @Nullable List<String> flags,
                            @Nullable Integer modelId, @Nullable Map<String, Integer> enchantments, @Nullable Integer amount,
                            @Nullable String color, @Nullable String nbt, @Nullable Integer durability, @Nullable String skullOwner,
                            @Nullable String skullTexture, @Nullable String trim, @Nullable String trimMaterial,
                            @Nullable String equippableSlot, @Nullable String equippableModel) {
        this.id = id;
        this.name = name;
        this.glow = glow;
        this.lore = lore;
        this.flags = flags;
        this.modelId = modelId;
        this.enchantments = enchantments;
        this.amount = amount;
        this.color = color;
        this.nbt = nbt;
        this.durability = durability;
        this.skullOwner = skullOwner;
        this.skullTexture = skullTexture;
        this.trim = trim;
        this.trimMaterial = trimMaterial;
        this.equippableSlot = equippableSlot;
        this.equippableModel = equippableModel;
    }

    public SerializableItem(String id, String name, @Nullable Boolean glow, @Nullable List<String> lore, @Nullable List<String> flags,
                            @Nullable Integer modelId, @Nullable Map<String, Integer> enchantments, @Nullable Integer amount,
                            @Nullable String color) {
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

    public ItemStack toItem(HMCWraps plugin, Player player) {
        return toItem(plugin, player, null);
    }

    @NotNull
    public ItemStack toItem(HMCWraps plugin, Player player, Material newType) {
        ItemStack origin = plugin.getHookAccessor().getItemFromHook(getId());
        if (origin == null) {
            origin = new ItemStack(newType == null ? Material.STRUCTURE_VOID : newType);
        }
        if (origin.getType().isAir()) {
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
            for (String flag : getFlags()) {
                try {
                    builder.flags(ItemFlag.valueOf(flag));
                } catch (IllegalArgumentException ignored) { }
            }
        }
        if (getEnchantments() != null) {
            getEnchantments().forEach((name, level) -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
                if (enchantment != null) {
                    builder.enchant(enchantment, level);
                }
            });
        }
        if (Boolean.TRUE.equals(isGlow())) {
            builder.glow();
        }
        var item = builder.build();
        if (origin.getType() == Material.PLAYER_HEAD) {
            if (getSkullOwner() != null) {
                var skullMeta = (SkullMeta) item.getItemMeta();
                var skullOwner = getSkullOwner();
                if ((skullOwner.equals("%player_uuid%") || skullOwner.equals("%player_name%")) && player != null) {
                    skullOwner = player.getName();
                }
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner));
                item.setItemMeta(skullMeta);
            }
            if (getSkullTexture() != null) {
                var skullBuilder = ItemBuilder.skull(item);
                skullBuilder.texture(getSkullTexture(), UUID.randomUUID());
                item = skullBuilder.build();
            }
        }
        if (getDurability() != null && item.getItemMeta() instanceof Damageable itemMeta) {
            var damage = item.getType().getMaxDurability() - getDurability();
            itemMeta.setDamage(damage);
            item.setItemMeta(itemMeta);
        }
        if (VersionUtil.trimsSupported() && getTrim() != null && getTrimMaterial() != null && item.getItemMeta() instanceof ArmorMeta armorMeta) {
            try {
                armorMeta.setTrim(new ArmorTrim(Registry.TRIM_MATERIAL.get(NamespacedKey.fromString(getTrimMaterial())), Registry.TRIM_PATTERN.get(NamespacedKey.fromString(getTrim()))));
                armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
                item.setItemMeta(armorMeta);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Failed to set trim " + getTrim() + " and material " + getTrimMaterial() + "! It seems to not be a valid trim. Please check your configuration!");
            }
        }
        if (getNbt() != null) {
            try {
                new NBTContainer(getNbt());
            } catch (NbtApiException e) {
                Bukkit.getLogger().warning("A provided NBT data is invalid in an item!");
            }
            var itemNbt = new NBTItem(item);
            var newNbt = NBT.parseNBT(getNbt());
            itemNbt.mergeCompound(newNbt);
            item = itemNbt.getItem();
        }
        return item;
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
    public Integer getDurability() {
        return durability;
    }

    @Nullable
    public Color getColor() {
        if (color == null) {
            return ((HMCWraps) Bukkit.getPluginManager().getPlugin("HMCWraps")).getHookAccessor().getColorFromHook(getId());
        }
        return StringUtil.colorFromString(color);
    }

    @Nullable
    public String getNbt() {
        return nbt;
    }

    @Nullable
    public String getSkullOwner() {
        return skullOwner;
    }

    @Nullable
    public String getSkullTexture() {
        return skullTexture;
    }

    @Nullable
    public String getTrim() {
        if (trim == null) {
            return ((HMCWraps) Bukkit.getPluginManager().getPlugin("HMCWraps")).getHookAccessor().getTrimPatternFromHook(getId());
        }
        return trim;
    }

    @Nullable
    public String getTrimMaterial() {
        if (trimMaterial == null) {
            return ((HMCWraps) Bukkit.getPluginManager().getPlugin("HMCWraps")).getHookAccessor().getTrimMaterialFromHook(getId());
        }
        return trimMaterial;
    }

    @Nullable
    public EquipmentSlot getEquippableSlot() {
        if (equippableSlot == null) {
            return ((HMCWraps) Bukkit.getPluginManager().getPlugin("HMCWraps")).getHookAccessor().getEquippableSlotFromHook(getId());
        }
        try {
            return EquipmentSlot.valueOf(equippableSlot);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("Failed to parse equippable slot " + equippableSlot + "! It seems to not be a valid slot. Please check your configuration!");
        }
        return null;
    }

    @Nullable
    public NamespacedKey getEquippableModel() {
        if (equippableModel == null) {
            return ((HMCWraps) Bukkit.getPluginManager().getPlugin("HMCWraps")).getHookAccessor().getEquippableModelFromHook(getId());
        }
        return NamespacedKey.fromString(equippableModel);
    }

}
