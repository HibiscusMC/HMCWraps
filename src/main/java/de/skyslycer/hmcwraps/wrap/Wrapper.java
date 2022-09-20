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
    private final NamespacedKey wrapKey;
    private final NamespacedKey playerKey;
    private final NamespacedKey unwrapperKey;
    private final NamespacedKey wrapperKey;

    public Wrapper(HMCWraps plugin) {
        this.plugin = plugin;
        physicalKey = new NamespacedKey(plugin, "wrap-physical");
        wrapKey = new NamespacedKey(plugin, "wrap-id");
        playerKey = new NamespacedKey(plugin, "wrap-player");
        unwrapperKey = new NamespacedKey(plugin, "unwrapper");
        wrapperKey = new NamespacedKey(plugin, "wrapper");
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
        var data = container.get(wrapKey, PersistentDataType.STRING);
        if (data == null) {
            return null;
        }
        return plugin.getWraps().get(data);
    }

    @Override
    public ItemStack setWrap(Integer modelId, String wrapId, ItemStack target, boolean physical, Player player, boolean giveBack) {
        var editing = target.clone();
        var currentWrap = getWrap(editing);
        if (isPhysical(editing) && currentWrap != null && currentWrap.getPhysical().isPresent() && currentWrap.getPhysical().get().isKeepAfterUnwrap()
                && giveBack) {
            PlayerUtil.give(player, setWrapper(currentWrap.getPhysical().get().toItem(plugin, player), currentWrap.getUuid()));
        }
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(physicalKey, PersistentDataType.BYTE, (byte) (physical ? 1 : 0));
        meta.getPersistentDataContainer().set(wrapKey, PersistentDataType.STRING, wrapId);
        meta.setCustomModelData(modelId);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public ItemStack removeWrap(ItemStack itemStack, Player player, boolean giveBack) {
        return setWrap(null, "-", itemStack, false, player, giveBack);
    }

    @Override
    public ItemStack setUnwrapper(ItemStack item) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(unwrapperKey, PersistentDataType.BYTE, (byte) 1);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public ItemStack setWrapper(ItemStack item, String wrapId) {
        var editing = item.clone();
        var meta = editing.getItemMeta();
        meta.getPersistentDataContainer().set(wrapperKey, PersistentDataType.STRING, wrapId);
        editing.setItemMeta(meta);
        return editing;
    }

    @Override
    public boolean isUnwrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        return meta.getPersistentDataContainer().has(unwrapperKey, PersistentDataType.BYTE);
    }

    @Override
    public String getWrapper(ItemStack item) {
        var meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        return meta.getPersistentDataContainer().get(wrapperKey, PersistentDataType.STRING);
    }

}
