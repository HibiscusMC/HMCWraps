package de.skyslycer.hmcwraps.storage;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerFilterStorage implements Storage<Player, Boolean> {

    private final NamespacedKey key;
    private final HMCWrapsPlugin plugin;

    public PlayerFilterStorage(HMCWrapsPlugin plugin) {
        this.key = new NamespacedKey(plugin, "filterEnabled");
        this.plugin = plugin;
    }


    @Override
    public Boolean get(Player source) {
        var pdc = source.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
        if (!plugin.getConfiguration().getFavorites().isEnabled()) {
            return false;
        }
        if (pdc == null) {
            return plugin.getConfiguration().getFilter().getDefault();
        }
        return pdc == 1;
    }

    @Override
    public void set(Player source, Boolean value) {
        source.getPersistentDataContainer().set(key, PersistentDataType.BYTE, value ? (byte) 1 : (byte) 0);
    }

}
