package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.IWrap;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import java.util.UUID;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Wrapper implements IWrapper {

    private final HMCWraps plugin;

    private final NamespacedKey physicalKey;
    private final NamespacedKey wrapIdKey;
    private final NamespacedKey playerKey;
    private final NamespacedKey physicalUnwrapperKe;
    private final NamespacedKey physicalWrapperKey;
    private final NamespacedKey originalModelIdKey;

    public Wrapper(HMCWraps plugin) {
        this.plugin = plugin;
        physicalKey = new NamespacedKey(plugin, "wrap-physical");
        wrapIdKey = new NamespacedKey(plugin, "wrap-id");
        playerKey = new NamespacedKey(plugin, "wrap-player");
        physicalUnwrapperKey = new NamespacedKey(plugin, "unwrapper");
        physicalWrapperKey = new NamespacedKey(plugin, "wrapper");
        originalModelIdKey = new NamespacedKey(plugin, "original-model-id");
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
    public IWrap getWrap(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(wrapIdKey, PersistentDataType.STRING);
        if (data == null || data.equals("-")) {
            return null;
        }
        return plugin.getWraps().get(data);
    }

    @Override
    public ItemStack setWrap(Integer modelId, String wrapId, ItemStack target, boolean physical, Player player, boolean giveBack, boolean unwrap) {
        var editing = target.clone();
        var currentWrap = getWrap(editing);
        if (isPhysical(editing) && currentWrap != null && currentWrap.getPhysical().isPresent() && currentWrap.getPhysical().get().isKeepAfterUnwrap()
                && giveBack) {
            PlayerUtil.give(player, setWrapper(currentWrap.getPhysical().get().toItem(plugin, player), currentWrap.getUuid()));
        }
        var meta = editing.getItemMeta();
        var originalModelId = meta.getCustomModelData();
        meta.getPersistentDataContainer().set(physicalKey, PersistentDataType.BYTE, (byte) (physical ? 1 : 0));
        meta.getPersistentDataContainer().set(wrapIdKey, PersistentDataType.STRING, wrapId);
        meta.setCustomModelData(modelId);
        editing.setItemMeta(meta);
        if (unwrap) {
            return editing;
        }
        return setOriginalModelId(editing, originalModelId);
    }

    @Override
    public ItemStack setWrap(Integer modelId, String wrapId, ItemStack target, boolean physical, Player player, boolean giveBack) {
        return setWrap(modelId, wrapId, target, physical, player, giveBack, false)
    }

    @Override
    public ItemStack removeWrap(ItemStack item, Player player, boolean giveBack) {
        var currentWrap = getWrap(item);
        if (currentWrap == null) {
            return item;
        }
        return setWrap(getOriginalModelId(item), "-", item, false, player, giveBack, true);
    }

    @Override
    public ItemStack setUnwrapper(ItemStack item) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalUnwrapperKey, PersistentDataType.BYTE, (byte) 1);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public ItemStack setWrapper(ItemStack item, String wrapId) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalWrapperKey, PersistentDataType.STRING, wrapId);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isUnwrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(physicalUnwrapperKey, PersistentDataType.BYTE);
    }

    @Override
    public String getWrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(physicalWrapperKey, PersistentDataType.STRING);
    }

    @Override
    public int getOriginalModelId(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return 0;
        }
        if (plugin.getConfiguration().getModelIdSettings().isOriginalModelIdsEnabled()) {
            var data =  meta.getPersistentDataContainer().get(originalModelIdKey, PersistentDataType.INTEGER);
            if (data != null) {
                return data;
            }
        }
        if (plugin.getConfiguration().getModelIdSettings().isDefaultModelIdsEnabled()) {
            var map = plugin.getConfiguration().getModelIdSettings().getDefaultModelIds();
            if (map.containsKey(item.getType().toString())) {
                return map.get(item.getType().toString());
            }
            for (String key : map.keySet()) {
                if (plugin.getCollection().getMaterials(key).contains(item.getType())) {
                    return map.get(key);
                }
            }
        }
        return 0;
    }

    @Override
    public ItemStack setOriginalModelId(ItemStack item, int originalModelid) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(originalModelIdKey, PersistentDataType.INTEGER, originalModelid);
        editing.setItemMeta(meta);
        return editing;
    }

}
