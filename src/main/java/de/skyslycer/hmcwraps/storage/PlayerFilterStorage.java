package de.skyslycer.hmcwraps.storage;

import de.skyslycer.hmcwraps.HMCWraps;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class PlayerFilterStorage implements Storage<Player, Boolean> {

    private final NamespacedKey key;

    public PlayerFilterStorage(HMCWraps plugin) {
        this.key = new NamespacedKey(plugin, "filterEnabled");
    }


    @Override
    public Boolean get(Player source) {
        var pdc = source.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
        return pdc != null && pdc == 1;
    }

    @Override
    public void set(Player source, Boolean value) {
        source.getPersistentDataContainer().set(key, PersistentDataType.BYTE, value ? (byte) 1 : (byte) 0);
    }

}
