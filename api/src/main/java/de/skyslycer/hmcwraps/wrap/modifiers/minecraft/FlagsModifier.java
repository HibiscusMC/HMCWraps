package de.skyslycer.hmcwraps.wrap.modifiers.minecraft;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlagsModifier implements WrapModifier {

    private final NamespacedKey originalFlagsKey;

    private final HMCWraps plugin;

    public FlagsModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalFlagsKey = new NamespacedKey(plugin, "original-flags");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        var meta = item.getItemMeta();
        var originalFlags = getOriginalFlags(item);
        var currentFlags = meta.getItemFlags().stream().toList();
        if (currentWrap != null) {
            meta.removeItemFlags(meta.getItemFlags().toArray(ItemFlag[]::new));
            if (originalFlags != null) {
                meta.addItemFlags(originalFlags.toArray(ItemFlag[]::new));
            }
        }
        if (wrap != null && wrap.getWrapFlags() != null) {
            for (String flag : wrap.getWrapFlags()) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                } catch (IllegalArgumentException ignored) { }
            }
        }
        if (wrap == null) {
            meta.getPersistentDataContainer().remove(originalFlagsKey);
        }
        item.setItemMeta(meta);
        if (wrap != null && currentWrap == null) {
            setOriginalFlags(item, currentFlags);
        }
    }

    private void setOriginalFlags(ItemStack item, List<ItemFlag> flags) {
        var meta = item.getItemMeta();
        if (flags != null && !flags.isEmpty()) {
            meta.getPersistentDataContainer().set(originalFlagsKey, PersistentDataType.STRING,
                    flags.stream().map(ItemFlag::toString).collect(Collectors.joining(SEPARATOR)));
        } else {
            meta.getPersistentDataContainer().remove(originalFlagsKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the original flags of an item.
     *
     * @param item The item
     * @return The original flags
     */
    public List<ItemFlag> getOriginalFlags(ItemStack item) {
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

}
