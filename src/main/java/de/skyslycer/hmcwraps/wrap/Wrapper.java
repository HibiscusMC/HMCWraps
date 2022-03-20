package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.Wrap;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Wrapper {

    private final HMCWraps plugin;

    private final NamespacedKey physicalKey;
    private final NamespacedKey wrapKey;
    private final NamespacedKey unwrapperKey;
    private final NamespacedKey wrapperKey;

    public Wrapper(HMCWraps plugin) {
        this.plugin = plugin;
        physicalKey = new NamespacedKey(plugin, "wrap-physical");
        wrapKey = new NamespacedKey(plugin, "wrap-id");
        unwrapperKey = new NamespacedKey(plugin, "unwrapper");
        wrapperKey = new NamespacedKey(plugin, "wrapper");
    }

    public boolean isPhysical(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(physicalKey, PersistentDataType.BYTE);
        if (data == null) {
            return false;
        }
        return data.intValue() > 0;
    }

    public Wrap getWrap(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        var data = container.get(wrapKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return plugin.getWraps().get(data);
    }

    public ItemStack setWrap(Integer modelId, String wrapId, ItemStack target, boolean physical, Player player) {
        var editing = target.clone();
        var currentWrap = getWrap(editing);
        if (isPhysical(editing) && currentWrap != null && currentWrap.getPhysical() != null && currentWrap.getPhysical().isKeepAfterUnwrap()) {
            PlayerUtil.give(player, setWrapper(currentWrap.getPhysical().toItem(plugin, player), currentWrap.getUuid()));
        }
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalKey, PersistentDataType.BYTE, (byte) (physical ? 1 : 0));
        meta.getPersistentDataContainer().set(wrapKey, PersistentDataType.STRING, wrapId);
        meta.setCustomModelData(modelId);
        editing.setItemMeta(meta);
        return editing;
    }

    public ItemStack removeWrap(ItemStack itemStack, Player player) {
        return setWrap(null, "-", itemStack, false, player);
    }

    public ItemStack setUnwrapper(ItemStack item) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(unwrapperKey, PersistentDataType.BYTE, (byte) 1);
        editing.setItemMeta(meta);
        return editing;
    }

    public ItemStack setWrapper(ItemStack item, String wrapId) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(wrapperKey, PersistentDataType.STRING, wrapId);
        editing.setItemMeta(meta);
        return editing;
    }

    public boolean isUnwrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(unwrapperKey, PersistentDataType.BYTE);
    }

    public String getWrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(wrapperKey, PersistentDataType.STRING);
    }

}
