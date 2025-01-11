package de.skyslycer.hmcwraps.wrap;

import com.nexomc.nexo.api.NexoItems;
import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.events.ItemUnwrapEvent;
import de.skyslycer.hmcwraps.events.ItemWrapEvent;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap.WrapValues;
import de.skyslycer.hmcwraps.serialization.wrap.range.ValueRangeSettings;
import de.skyslycer.hmcwraps.util.*;
import de.tr7zw.changeme.nbtapi.NBT;
import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class WrapperImpl implements Wrapper {

    private static final String SEPARATOR = ";!;";

    private final HMCWrapsPlugin plugin;

    private final NamespacedKey physicalKey;
    private final NamespacedKey wrapIdKey;
    private final NamespacedKey playerKey;
    private final NamespacedKey physicalUnwrapperKey;
    private final NamespacedKey physicalWrapperKey;
    private final NamespacedKey originalModelIdKey;
    private final NamespacedKey originalColorKey;
    private final NamespacedKey originalNameKey;
    private final NamespacedKey originalLoreKey;
    private final NamespacedKey originalFlagsKey;
    private final NamespacedKey originalItemsAdderKey;
    private final NamespacedKey originalOraxenKey;
    private final NamespacedKey originalMythicKey;
    private final NamespacedKey originalNexoKey;
    private final NamespacedKey originalMaterialKey;
    private final NamespacedKey originalTrimKey;
    private final NamespacedKey originalTrimMaterialKey;
    private final NamespacedKey fakeDurabilityKey;
    private final NamespacedKey fakeMaxDurabilityKey;
    private final NamespacedKey customAttributesKey;
    private final NamespacedKey trimsUsedKey;
    private final NamespacedKey originalEquippableSlotKey;
    private final NamespacedKey originalEquippableModelKey;
    private final NamespacedKey originalGlintKey;

    public WrapperImpl(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
        physicalKey = new NamespacedKey(plugin, "wrap-physical");
        wrapIdKey = new NamespacedKey(plugin, "wrap-id");
        playerKey = new NamespacedKey(plugin, "wrap-player");
        physicalUnwrapperKey = new NamespacedKey(plugin, "unwrapper");
        physicalWrapperKey = new NamespacedKey(plugin, "wrapper");
        originalModelIdKey = new NamespacedKey(plugin, "original-model-id");
        originalColorKey = new NamespacedKey(plugin, "original-color");
        originalNameKey = new NamespacedKey(plugin, "original-name");
        originalLoreKey = new NamespacedKey(plugin, "original-lore");
        originalFlagsKey = new NamespacedKey(plugin, "original-flags");
        originalItemsAdderKey = new NamespacedKey(plugin, "original-itemsadder-id");
        originalOraxenKey = new NamespacedKey(plugin, "original-oraxen-id");
        originalMythicKey = new NamespacedKey(plugin, "original-mythic-id");
        originalNexoKey = new NamespacedKey(plugin, "original-nexo-id");
        originalMaterialKey = new NamespacedKey(plugin, "original-material");
        originalTrimKey = new NamespacedKey(plugin, "original-trim");
        originalTrimMaterialKey = new NamespacedKey(plugin, "original-trim-material");
        fakeDurabilityKey = new NamespacedKey(plugin, "fake-durability");
        fakeMaxDurabilityKey = new NamespacedKey(plugin, "fake-max-durability");
        customAttributesKey = new NamespacedKey(plugin, "custom-attributes");
        trimsUsedKey = new NamespacedKey(plugin, "trims-used");
        originalEquippableSlotKey = new NamespacedKey(plugin, "original-equippable-slot");
        originalEquippableModelKey = new NamespacedKey(plugin, "original-equippable-model");
        originalGlintKey = new NamespacedKey(plugin, "original-glint");
    }

    @Override
    public Wrap getWrap(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(wrapIdKey, PersistentDataType.STRING);
        if (data == null || data.equals("-")) {
            return null;
        }
        return plugin.getWrapsLoader().getWraps().get(data);
    }

    @Override
    public ItemStack setWrap(@Nullable Wrap wrap, ItemStack item, boolean physical, Player player) {
        var event = new ItemWrapEvent(wrap, item, physical, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return item;
        }
        return setWrapPrivate(event.getWrap(), event.getItem(), event.isPhysical(), event.getPlayer());
    }

    private ItemStack setWrapPrivate(@Nullable Wrap wrap, ItemStack item, boolean physical, Player player) {
        if (item == null || item.getType().isAir()) {
            return item;
        }
        var editing = item.clone();
        var currentWrap = getWrap(editing);
        if (isPhysical(editing) && currentWrap != null && currentWrap.getPhysical() != null && currentWrap.getPhysical().isKeepAfterUnwrap()) {
            PlayerUtil.give(player, setPhysicalWrapper(currentWrap.getPhysical().toItem(plugin, player), currentWrap));
        }
        var originalData = getOriginalData(item);
        var meta = editing.getItemMeta();
        var originalName = meta.getDisplayName();
        var originalModelId = -1;
        var originalLore = meta.getLore();
        var originalFlags = meta.getItemFlags().stream().toList();
        String originalMaterial = null;
        var originalItemsAdderId = getOriginalItemsAdderId(item);
        var originalOraxenId = getOriginalOraxenId(item);
        var originalMythicId = getOriginalMythicId(item);
        var originalNexoId = getOriginalNexoId(item);
        Boolean originalGlintOverride = VersionUtil.hasDataComponents() && meta.hasEnchantmentGlintOverride() ? meta.getEnchantmentGlintOverride() : null;
        String originalTrimMaterial = null;
        String originalTrim = null;
        if (VersionUtil.trimsSupported() && meta instanceof ArmorMeta armorMeta && armorMeta.getTrim() != null) {
            originalTrim = armorMeta.getTrim().getPattern().getKey().toString();
            originalTrimMaterial = armorMeta.getTrim().getMaterial().getKey().toString();
        }
        NamespacedKey originalEquippableModel = null;
        EquipmentSlot originalEquippableSlot = null;
        if (VersionUtil.equippableSupported() && meta.hasEquippable()) {
            originalEquippableModel = meta.getEquippable().getModel();
            originalEquippableSlot = meta.getEquippable().getSlot();
        }
        Color originalColor = null;
        if (meta.hasCustomModelData()) {
            originalModelId = meta.getCustomModelData();
        }
        meta.getPersistentDataContainer().remove(trimsUsedKey);
        meta.getPersistentDataContainer().set(wrapIdKey, PersistentDataType.STRING, wrap == null ? "-" : wrap.getUuid());
        meta.getPersistentDataContainer().remove(playerKey);
        meta.setCustomModelData(wrap == null ? originalData.modelId() : wrap.getModelId());
        if (currentWrap != null) {
            if (currentWrap.getWrapName() != null && (!Boolean.TRUE.equals(currentWrap.isApplyNameOnlyEmpty()) ||
                    StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, currentWrap.getWrapName())).equals(meta.getDisplayName()))) {
                meta.setDisplayName(originalData.name());
            }
            if (currentWrap.getWrapLore() != null) {
                meta.setLore(originalData.lore());
            }
            meta.removeItemFlags(meta.getItemFlags().toArray(ItemFlag[]::new));
            if (originalData.flags() != null) {
                meta.addItemFlags(originalData.flags().toArray(ItemFlag[]::new));
            }
            if (VersionUtil.hasDataComponents()) {
                meta.setEnchantmentGlintOverride(originalData.glintOverride());
            }
        }
        if (wrap != null) {
            editing.setItemMeta(meta);
            if (currentWrap != null && originalData.material() != null && !originalData.material().isBlank()) {
                switchFromAlternative(editing, originalData.material());
            }
            resetFakeDurability(item, editing);
            meta = editing.getItemMeta();
            var originalActualName = currentWrap == null ? originalName : originalData.name();
            if (wrap.getWrapName() != null && (!Boolean.TRUE.equals(wrap.isApplyNameOnlyEmpty()) || originalActualName == null || originalActualName.isBlank())) {
                meta.setDisplayName(StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, wrap.getWrapName())).replace("%originalname%", originalActualName == null ? "" : originalActualName));
            }
            if (wrap.getWrapLore() != null) {
                var lore = wrap.getWrapLore().stream().map(entry -> StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, entry))).toList();
                meta.setLore(lore);
            }
            if (wrap.getWrapFlags() != null) {
                for (String flag : wrap.getWrapFlags()) {
                    try {
                        meta.addItemFlags(ItemFlag.valueOf(flag));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            if (wrap.getArmorImitationType() != null && wrap.getArmorImitationType().equalsIgnoreCase("LEATHER")) {
                meta.addItemFlags(ItemFlag.HIDE_DYE);
            }
            editing.setItemMeta(meta);
            var changedDurability = false;
            if (MaterialUtil.getAlternative(wrap.getArmorImitationType(), editing.getType()) != editing.getType()) {
                var maxDurability = editing.getType().getMaxDurability();
                var currentDurability = maxDurability - ((Damageable) meta).getDamage();
                var temp = editing.getType().toString();
                var attributeModifiers = editing.getItemMeta().getAttributeModifiers();
                if (switchToAlternative(editing, wrap.getArmorImitationType())) {
                    int newDurability = editing.getType().getMaxDurability();
                    var modelDurability = ((double) currentDurability / maxDurability) * newDurability;
                    var newMeta = ((Damageable) editing.getItemMeta());
                    newMeta.setDamage(newDurability - (int) modelDurability);
                    newMeta.getPersistentDataContainer().set(fakeDurabilityKey, PersistentDataType.INTEGER, currentDurability);
                    newMeta.getPersistentDataContainer().set(fakeMaxDurabilityKey, PersistentDataType.INTEGER, (int) maxDurability);
                    newMeta.getPersistentDataContainer().set(customAttributesKey, PersistentDataType.BOOLEAN, attributeModifiers != null);
                    editing.setItemMeta(newMeta);
                    originalMaterial = temp;
                    changedDurability = true;
                }
            }
            if (wrap.getWrapDurability() != null && wrap.getWrapDurability() > 0) {
                var maxDurability = editing.getType().getMaxDurability();
                var currentDurability = maxDurability - ((Damageable) meta).getDamage();
                var modelDurability = ((double) currentDurability / maxDurability) * wrap.getWrapDurability();
                var newMeta = ((Damageable) editing.getItemMeta());
                newMeta.setDamage(maxDurability - currentDurability);
                newMeta.getPersistentDataContainer().set(fakeDurabilityKey, PersistentDataType.INTEGER, (int) modelDurability);
                newMeta.getPersistentDataContainer().set(fakeMaxDurabilityKey, PersistentDataType.INTEGER, wrap.getWrapDurability());
                editing.setItemMeta(newMeta);
                changedDurability = true;
            }
            if (!changedDurability) {
                var newMeta = editing.getItemMeta();
                newMeta.getPersistentDataContainer().remove(fakeDurabilityKey);
                newMeta.getPersistentDataContainer().remove(fakeMaxDurabilityKey);
                editing.setItemMeta(newMeta);
            }
            if (wrap.getColor() != null && editing.getItemMeta() instanceof LeatherArmorMeta leatherMeta) {
                originalColor = leatherMeta.getColor();
                leatherMeta.setColor(wrap.getColor());
                editing.setItemMeta(leatherMeta);
            }
            if (VersionUtil.trimsSupported() && editing.getItemMeta() instanceof ArmorMeta armorMeta) {
                if (wrap.isRemoveTrim() == Boolean.TRUE) {
                    armorMeta.setTrim(null);
                    editing.setItemMeta(armorMeta);
                }
                if (wrap.getTrim() != null && wrap.getTrimMaterial() != null) {
                    try {
                        armorMeta.setTrim(new ArmorTrim(Registry.TRIM_MATERIAL.get(NamespacedKey.fromString(wrap.getTrimMaterial())), Registry.TRIM_PATTERN.get(NamespacedKey.fromString(wrap.getTrim()))));
                        armorMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
                        armorMeta.getPersistentDataContainer().set(trimsUsedKey, PersistentDataType.BOOLEAN, true);
                        editing.setItemMeta(armorMeta);
                    } catch (IllegalArgumentException exception) {
                        plugin.getLogger().warning("Failed to set trim for item " + wrap.getUuid() + " with trim " + wrap.getTrim() + " and material " + wrap.getTrimMaterial() + "! It seems to not be a valid trim. Please check your configuration!");
                    }
                }
            }
            if (VersionUtil.equippableSupported() && ((wrap.getEquippableSlot() != null && wrap.getEquippableModel() != null)
                    || (wrap.getEquippableSlot() != null && wrap.getEquippableSlot() == EquipmentSlot.HEAD))) {
                var newMeta = editing.getItemMeta();
                var equippable = newMeta.getEquippable();
                equippable.setSlot(wrap.getEquippableSlot());
                if (wrap.getEquippableModel() != null) {
                    equippable.setModel(wrap.getEquippableModel());
                }
                newMeta.setEquippable(equippable);
                editing.setItemMeta(newMeta);
            }
            if (VersionUtil.hasDataComponents() && wrap.isGlintOverride() != null) {
                var newMeta = editing.getItemMeta();
                newMeta.setEnchantmentGlintOverride(wrap.isGlintOverride());
                editing.setItemMeta(newMeta);
            }
            if (wrap.getWrapNbt() != null) {
                WrapNBTUtil.wrap(editing, StringUtil.replacePlaceholders(player, wrap.getWrapNbt()));
            }
            if (wrap.getId().startsWith("itemsadder:")) {
                setItemsAdderNBT(editing, wrap.getId().substring(11));
            }
        } else {
            if (meta instanceof LeatherArmorMeta leatherMeta) {
                leatherMeta.setColor(originalData.color());
                editing.setItemMeta(leatherMeta);
            } else {
                editing.setItemMeta(meta);
            }
            if (VersionUtil.trimsSupported() && editing.getItemMeta() instanceof ArmorMeta armorMeta) {
                try {
                    if (originalData.trim() == null || originalData.trimMaterial() == null) {
                        armorMeta.setTrim(null);
                    } else {
                        armorMeta.setTrim(new ArmorTrim(Registry.TRIM_MATERIAL.get(NamespacedKey.fromString(originalData.trimMaterial())),
                                Registry.TRIM_PATTERN.get(NamespacedKey.fromString(originalData.trim()))));
                    }
                    editing.setItemMeta(armorMeta);
                } catch (IllegalArgumentException exception) {
                    plugin.getLogger().warning("Failed to set trim for player " + player.getName() + " with trim " + originalData.trim() + " and material " + originalData.trimMaterial() + "! It seems to not be a valid trim. This is being set while unwrapping to preserve the original trim, which has since been removed.");
                }
            }
            if (VersionUtil.equippableSupported()) {
                var newMeta = editing.getItemMeta();
                if ((originalData.equippableSlot() != null && originalData.equippableModel() != null)
                        || (originalData.equippableSlot() != null && originalData.equippableSlot() == EquipmentSlot.HEAD)) {
                    var equippable = newMeta.getEquippable();
                    equippable.setSlot(originalData.equippableSlot());
                    if (originalData.equippableModel() != null) {
                        equippable.setModel(originalData.equippableModel());
                    }
                    newMeta.setEquippable(equippable);
                } else {
                    newMeta.setEquippable(null);
                }
                editing.setItemMeta(newMeta);
            }
            if (VersionUtil.hasDataComponents()) {
                var newMeta = editing.getItemMeta();
                newMeta.setEnchantmentGlintOverride(originalData.glintOverride());
                editing.setItemMeta(newMeta);
            }
            if (originalData.material() != null && !originalData.material().isBlank()) {
                switchFromAlternative(editing, originalData.material());
            }
            resetFakeDurability(item, editing);
            WrapNBTUtil.unwrap(editing);
            if (originalData.itemsAdder() != null || (currentWrap != null && currentWrap.getId().startsWith("itemsadder:"))) {
                setItemsAdderNBT(editing, originalData.itemsAdder());
            }
        }
        editing = setPhysical(editing.clone(), physical);
        if (wrap == null || currentWrap != null) {
            return editing;
        }
        return setOriginalData(editing, new WrapValues(originalModelId, originalColor, originalName, originalLore,
                originalFlags, originalItemsAdderId, originalOraxenId, originalMythicId, originalNexoId, originalMaterial,
                originalTrim, originalTrimMaterial, originalEquippableModel, originalEquippableSlot, originalGlintOverride));
    }

    private void setItemsAdderNBT(ItemStack item, String id) {
        NBT.modify(item, nbt -> {
            var split = id != null ? id.split(":") : new String[0];
            var iaCompound = nbt.getCompound("itemsadder");
            if (iaCompound != null) {
                iaCompound.removeKey("namespace");
                iaCompound.removeKey("id");
                if (iaCompound.getKeys().isEmpty()) {
                    nbt.removeKey("itemsadder");
                }
            }
            if (split.length == 2) {
                iaCompound = nbt.getOrCreateCompound("itemsadder");
                iaCompound.setString("namespace", split[0]);
                iaCompound.setString("id", split[1]);
            }
        });
    }

    private void resetFakeDurability(ItemStack item, ItemStack editing) {
        if (getFakeDurability(item) != -1) {
            var newMeta = (Damageable) editing.getItemMeta();
            if (newMeta.getPersistentDataContainer().has(fakeDurabilityKey, PersistentDataType.INTEGER)) {
                var currentDurability = getFakeDurability(item);
                var oldMaxDurability = getFakeMaxDurability(item);
                var newMaxDurability = editing.getType().getMaxDurability();
                var newDurability = ((double) currentDurability / oldMaxDurability) * newMaxDurability;
                newMeta.setDamage(editing.getType().getMaxDurability() - (int) newDurability);
                newMeta.getPersistentDataContainer().remove(fakeDurabilityKey);
                newMeta.getPersistentDataContainer().remove(fakeMaxDurabilityKey);
                editing.setItemMeta(newMeta);
            }
        }
    }

    private boolean switchToAlternative(ItemStack editing, String alternative) {
        var newMaterial = MaterialUtil.getAlternative(alternative, editing.getType());
        var hasCustomAttributes = editing.getItemMeta().getAttributeModifiers() != null;
        var armorModifiers = ArmorModifiers.getFromMaterial(editing.getType().toString());
        editing.setType(newMaterial);
        if (!hasCustomAttributes) {
            if (editing.getType().toString().contains("_HELMET")) {
                ArmorModifiers.applyAttributes(editing, EquipmentSlot.HEAD, armorModifiers.getToughness(), armorModifiers.getKnockback(), armorModifiers.getDefense().helmet());
            } else if (editing.getType().toString().contains("_CHESTPLATE")) {
                ArmorModifiers.applyAttributes(editing, EquipmentSlot.CHEST, armorModifiers.getToughness(), armorModifiers.getKnockback(), armorModifiers.getDefense().chestplate());
            } else if (editing.getType().toString().contains("_LEGGINGS")) {
                ArmorModifiers.applyAttributes(editing, EquipmentSlot.LEGS, armorModifiers.getToughness(), armorModifiers.getKnockback(), armorModifiers.getDefense().leggings());
            } else if (editing.getType().toString().contains("_BOOTS")) {
                ArmorModifiers.applyAttributes(editing, EquipmentSlot.FEET, armorModifiers.getToughness(), armorModifiers.getKnockback(), armorModifiers.getDefense().boots());
            } else {
                return false;
            }
        }
        return true;
    }

    private void switchFromAlternative(ItemStack editing, String material) {
        editing.setType(Material.valueOf(material));
        if (!isCustomAttributes(editing)) {
            ArmorModifiers.removeAttributes(editing);
        }
    }

    @Override
    public int getFakeDurability(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return -1;
        }
        var data = meta.getPersistentDataContainer().get(fakeDurabilityKey, PersistentDataType.INTEGER);
        if (data == null) {
            return -1;
        }
        return data;
    }

    @Override
    public void setFakeDurability(ItemStack item, int durability) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(fakeDurabilityKey, PersistentDataType.INTEGER, durability);
        item.setItemMeta(meta);
    }

    @Override
    public int getFakeMaxDurability(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return -1;
        }
        var data = meta.getPersistentDataContainer().get(fakeMaxDurabilityKey, PersistentDataType.INTEGER);
        if (data == null) {
            return -1;
        }
        return data;
    }

    @Override
    public void setFakeMaxDurability(ItemStack item, int durability) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return;
        }
        meta.getPersistentDataContainer().set(fakeDurabilityKey, PersistentDataType.INTEGER, durability);
        item.setItemMeta(meta);
    }

    @Override
    public ItemStack removeWrap(ItemStack target, Player player) {
        var event = new ItemUnwrapEvent(target, player, getWrap(target));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return target;
        }
        return removeWrapPrivate(event.getItem(), event.getPlayer());
    }

    private ItemStack removeWrapPrivate(ItemStack item, Player player) {
        var currentWrap = getWrap(item);
        if (currentWrap == null) {
            return item;
        }
        return setWrapPrivate(null, item, false, player);
    }

    @Override
    public ItemStack setPhysicalUnwrapper(ItemStack item) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalUnwrapperKey, PersistentDataType.BYTE, (byte) 1);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isPhysicalUnwrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(physicalUnwrapperKey, PersistentDataType.BYTE);
    }

    @Override
    public ItemStack setPhysicalWrapper(ItemStack item, Wrap wrap) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalWrapperKey, PersistentDataType.STRING, wrap.getUuid());
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public String getPhysicalWrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(physicalWrapperKey, PersistentDataType.STRING);
    }

    @Override
    public ItemStack setTrimsUsed(ItemStack item, boolean used) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(trimsUsedKey, PersistentDataType.BOOLEAN, used);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isTrimsUsed(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(trimsUsedKey, PersistentDataType.BOOLEAN);
    }

    @Override
    public WrapValues getOriginalData(ItemStack item) {
        return new WrapValues(getOriginalModelId(item), getOriginalColor(item), getOriginalName(item),
                getOriginalLore(item), getOriginalFlags(item), getOriginalItemsAdderId(item), getOriginalOraxenId(item),
                getOriginalMythicId(item), getOriginalNexoId(item), getOriginalMaterial(item), getOriginalTrim(item), getOriginalTrimMaterial(item),
                getOriginalEquippableModel(item), getOriginalEquippableSlot(item), getOriginalGlint(item));
    }

    private String getOriginalTrim(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalTrimKey, PersistentDataType.STRING);
    }

    private String getOriginalTrimMaterial(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalTrimMaterialKey, PersistentDataType.STRING);
    }

    private EquipmentSlot getOriginalEquippableSlot(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(originalEquippableSlotKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return EquipmentSlot.valueOf(data);
    }

    private NamespacedKey getOriginalEquippableModel(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(originalEquippableModelKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return NamespacedKey.fromString(data);
    }

    private int getOriginalModelId(ItemStack item) {
        var meta = item.getItemMeta();
        var modelData = -1;
        var modelDataSettings = plugin.getConfiguration().getPreservation().getModelId();
        if (modelDataSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalModelIdKey, PersistentDataType.INTEGER);
            if (data != null) {
                modelData = data;
            }
        } else if (modelDataSettings.isDefaultEnabled()) {
            var map = modelDataSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                modelData = map.get(item.getType().toString());
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    modelData = map.get(key);
                }
            }
        }
        return modelData;
    }

    private String getOriginalName(ItemStack item) {
        var meta = item.getItemMeta();
        String name = null;
        var nameSettings = plugin.getConfiguration().getPreservation().getName();
        if (nameSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalNameKey, PersistentDataType.STRING);
            if (data != null) {
                name = ChatColor.translateAlternateColorCodes('&', data);
            }
            return name;
        } else if (nameSettings.isDefaultEnabled()) {
            var map = nameSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                name = StringUtil.LEGACY_SERIALIZER_AMPERSAND.serialize(StringUtil.parseComponent(map.get(item.getType().toString())));
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    name = StringUtil.LEGACY_SERIALIZER_AMPERSAND.serialize(StringUtil.parseComponent(map.get(key)));
                }
            }
            return name;
        }
        return item.getItemMeta().getDisplayName();
    }

    private List<String> getOriginalLore(ItemStack item) {
        var meta = item.getItemMeta();
        var lore = new ArrayList<String>();
        var loreSettings = plugin.getConfiguration().getPreservation().getLore();
        if (loreSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalLoreKey, PersistentDataType.STRING);
            if (data != null) {
                Arrays.stream(data.split(SEPARATOR)).map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
            }
            return lore;
        } else if (loreSettings.isDefaultEnabled()) {
            var map = loreSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                map.get(item.getType().toString()).stream().map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    map.get(key).stream().map(entry -> ChatColor.translateAlternateColorCodes('&', entry)).forEach(lore::add);
                }
            }
            return lore;
        }
        return item.getItemMeta().getLore();
    }

    private Color getOriginalColor(ItemStack item) {
        var colorSettings = plugin.getConfiguration().getPreservation().getColor();
        Color color = null;
        var meta = item.getItemMeta();
        if (colorSettings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalColorKey, PersistentDataType.INTEGER);
            if (data != null) {
                color = Color.fromRGB(data);
            }
        } else if (colorSettings.isDefaultEnabled()) {
            var map = colorSettings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                color = StringUtil.colorFromString(map.get(item.getType().toString()));
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    color = StringUtil.colorFromString(map.get(key));
                }
            }
        }
        return color;
    }

    private List<ItemFlag> getOriginalFlags(ItemStack item) {
        var settings = plugin.getConfiguration().getPreservation().getFlags();
        var list = new ArrayList<ItemFlag>();
        var meta = item.getItemMeta();
        if (settings.isOriginalEnabled()) {
            var data = meta.getPersistentDataContainer().get(originalFlagsKey, PersistentDataType.STRING);
            if (data != null) {
                var flags = data.split(SEPARATOR);
                for (String flag : flags) {
                    try {
                        list.add(ItemFlag.valueOf(flag));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            return list;
        } else if (settings.isDefaultEnabled()) {
            var map = settings.getDefaults();
            if (map.containsKey(item.getType().toString())) {
                for (String flag : map.get(item.getType().toString())) {
                    try {
                        list.add(ItemFlag.valueOf(flag));
                    } catch (IllegalArgumentException ignored) { }
                }
            }
            for (String key : map.keySet()) {
                if (plugin.getCollectionHelper().getMaterials(key).contains(item.getType())) {
                    for (String flag : map.get(key)) {
                        try {
                            list.add(ItemFlag.valueOf(flag));
                        } catch (IllegalArgumentException ignored) { }
                    }
                }
            }
            return list;
        }
        return item.getItemMeta().getItemFlags().stream().toList();
    }

    private String getOriginalItemsAdderId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalItemsAdderKey, PersistentDataType.STRING);
    }

    private String getOriginalOraxenId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalOraxenKey, PersistentDataType.STRING);
    }

    private String getOriginalMythicId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalMythicKey, PersistentDataType.STRING);
    }

    private String getOriginalNexoId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalNexoKey, PersistentDataType.STRING);
    }

    private String getOriginalMaterial(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var value = container.get(originalMaterialKey, PersistentDataType.STRING);
        return value == null ? "" : value;
    }

    private Boolean getOriginalGlint(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalGlintKey, PersistentDataType.BOOLEAN);
    }

    @Override
    public ItemStack setOriginalData(ItemStack item, WrapValues wrapValues) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(originalModelIdKey, PersistentDataType.INTEGER, wrapValues.modelId());
        if (wrapValues.color() != null) {
            meta.getPersistentDataContainer().set(originalColorKey, PersistentDataType.INTEGER, wrapValues.color().asRGB());
        }
        if (wrapValues.name() != null) {
            meta.getPersistentDataContainer().set(originalNameKey, PersistentDataType.STRING, wrapValues.name().replace("§", "&"));
        }
        if (wrapValues.lore() != null) {
            meta.getPersistentDataContainer().set(originalLoreKey, PersistentDataType.STRING,
                    wrapValues.lore().stream().map(entry -> entry.replace("§", "&")).collect(Collectors.joining(SEPARATOR)));
        }
        if (wrapValues.flags() != null) {
            meta.getPersistentDataContainer().set(originalFlagsKey, PersistentDataType.STRING,
                    wrapValues.flags().stream().map(ItemFlag::toString).collect(Collectors.joining(SEPARATOR)));
        }
        if (wrapValues.itemsAdder() != null) {
            meta.getPersistentDataContainer().set(originalItemsAdderKey, PersistentDataType.STRING, wrapValues.itemsAdder());
        }
        if (wrapValues.oraxen() != null) {
            meta.getPersistentDataContainer().set(originalOraxenKey, PersistentDataType.STRING, wrapValues.oraxen());
        }
        if (wrapValues.mythic() != null) {
            meta.getPersistentDataContainer().set(originalMythicKey, PersistentDataType.STRING, wrapValues.mythic());
        }
        if (wrapValues.nexo() != null) {
            meta.getPersistentDataContainer().set(originalNexoKey, PersistentDataType.STRING, wrapValues.nexo());
        }
        if (wrapValues.material() != null) {
            meta.getPersistentDataContainer().set(originalMaterialKey, PersistentDataType.STRING, wrapValues.material());
        }
        if (wrapValues.trim() != null) {
            meta.getPersistentDataContainer().set(originalTrimKey, PersistentDataType.STRING, wrapValues.trim());
        }
        if (wrapValues.trimMaterial() != null) {
            meta.getPersistentDataContainer().set(originalTrimMaterialKey, PersistentDataType.STRING, wrapValues.trimMaterial());
        }
        if (wrapValues.equippableSlot() != null) {
            meta.getPersistentDataContainer().set(originalEquippableSlotKey, PersistentDataType.STRING, wrapValues.equippableSlot().name());
        }
        if (wrapValues.equippableModel() != null) {
            meta.getPersistentDataContainer().set(originalEquippableModelKey, PersistentDataType.STRING, wrapValues.equippableModel().toString());
        }
        if (wrapValues.glintOverride() != null) {
            meta.getPersistentDataContainer().set(originalGlintKey, PersistentDataType.BOOLEAN, wrapValues.glintOverride());
        }
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public UUID getOwningPlayer(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(playerKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return UUID.fromString(data);
    }

    @Override
    public ItemStack setOwningPlayer(ItemStack item, UUID uuid) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(playerKey, PersistentDataType.STRING, uuid.toString());
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isOwningPlayer(ItemStack item, Player player) {
        var uuid = getOwningPlayer(item);
        if (uuid == null) {
            return false;
        }
        return player.getUniqueId().equals(uuid);
    }

    @Override
    public boolean isPhysical(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(physicalKey, PersistentDataType.BYTE);
        if (data == null) {
            return false;
        }
        return data.intValue() > 0;
    }

    @Override
    public ItemStack setPhysical(ItemStack item, boolean physical) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalKey, PersistentDataType.BYTE, physical ? (byte) 1 : (byte) 0);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isCustomAttributes(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(customAttributesKey, PersistentDataType.BOOLEAN);
        if (data == null) {
            return false;
        }
        return data;
    }

    @Override
    public boolean isValid(ItemStack item, Wrap wrap) {
        return wrap.getRange() == null || (isValidType(wrap.getRange().getModelId(), getRealModelId(item)) && isValidColor(wrap.getRange().getColor(), getRealColor(item)) &&
                isValidType(wrap.getRange().getItemsAdder(), getRealItemsAdderId(item))
                && isValidType(wrap.getRange().getOraxen(), getRealOraxenId(item))
                && isValidType(wrap.getRange().getMythic(), getRealMythicId(item))
                && isValidType(wrap.getRange().getNexo(), getRealNexoId(item)));
    }

    private <T> boolean isValidType(ValueRangeSettings<T> settings, T value) {
        if (settings == null) {
            return true;
        }
        return (settings.getExclude() == null || !settings.getExclude().contains(value)) && (settings.getInclude() == null || settings.getInclude().contains(value));
    }

    private boolean isValidColor(ValueRangeSettings<String> settings, Color value) {
        if (settings == null) {
            return true;
        }
        if (value.equals(StringUtil.colorFromString("#A06540"))) {
            value = null;
        }
        List<Color> exclude = null;
        List<Color> include = null;
        if (settings.getExclude() != null) {
            exclude = settings.getExclude().stream().map(StringUtil::colorFromString).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if (settings.getInclude() != null) {
            include = settings.getInclude().stream().map(StringUtil::colorFromString).filter(Objects::nonNull).collect(Collectors.toList());
        }
        if ((exclude != null && exclude.contains(value)) || (settings.getExclude() != null && settings.getExclude().contains("none") && value == null)) {
            return false;
        }
        if ((include != null && !include.contains(value)) && (settings.getInclude() != null && (!settings.getInclude().contains("none") || value != null))) {
            return false;
        }
        return true;
    }

    private int getRealModelId(ItemStack item) {
        var modelData = -1;
        if (getWrap(item) != null) {
            modelData = getOriginalModelId(item);
        } else if (item.getItemMeta().hasCustomModelData()) {
            modelData = item.getItemMeta().getCustomModelData();
        }
        return modelData;
    }

    private Color getRealColor(ItemStack item) {
        Color color = null;
        if (getWrap(item) != null) {
            color = getOriginalColor(item);
        } else if (item.getItemMeta() instanceof LeatherArmorMeta meta) {
            color = meta.getColor();
        }
        return color;
    }

    private String getRealItemsAdderId(ItemStack item) {
        String itemsAdderId = null;
        if (getWrap(item) != null) {
            itemsAdderId = getOriginalItemsAdderId(item);
        } else if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            var id = CustomStack.byItemStack(item);
            if (id != null) {
                itemsAdderId = id.getNamespacedID();
            }
        }
        return itemsAdderId;
    }

    private String getRealOraxenId(ItemStack item) {
        String oraxenId = null;
        if (getWrap(item) != null) {
            oraxenId = getOriginalOraxenId(item);
        } else if (Bukkit.getPluginManager().getPlugin("Oraxen") != null) {
            var id = OraxenItems.getIdByItem(item);
            if (id != null) {
                oraxenId = id;
            }
        }
        return oraxenId;
    }

    private String getRealMythicId(ItemStack item) {
        String mythicId = null;
        if (getWrap(item) != null) {
            mythicId = getOriginalMythicId(item);
        } else if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            var id = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
            if (id != null) {
                mythicId = id;
            }
        }
        return mythicId;
    }

    private String getRealNexoId(ItemStack item) {
        String nexoId = null;
        if (getWrap(item) != null) {
            nexoId = getOriginalNexoId(item);
        } else if (Bukkit.getPluginManager().getPlugin("Nexo") != null) {
            var id = NexoItems.idFromItem(item);
            if (id != null) {
                nexoId = id;
            }
        }
        return nexoId;
    }

    @Override
    public boolean isGloballyDisabled(ItemStack item) {
        if (plugin.getConfiguration().getGlobalDisable().getModelId().contains(getRealModelId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getColor().stream().map(StringUtil::colorFromString).toList().contains(getRealColor(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getItemsAdderId().contains(getRealItemsAdderId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getOraxenId().contains(getRealOraxenId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getMythicId().contains(getRealMythicId(item))) {
            return true;
        }
        if (plugin.getConfiguration().getGlobalDisable().getNexoId().contains(getRealNexoId(item))) {
            return true;
        }
        return false;
    }

}
