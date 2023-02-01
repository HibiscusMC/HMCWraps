package de.skyslycer.hmcwraps.storage;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.IWrap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteWrapStorage implements Storage<Player, List<IWrap>> {

    private static final String SEPARATOR = ";!;";

    private final NamespacedKey key;
    private final HMCWraps plugin;

    public FavoriteWrapStorage(HMCWraps plugin) {
        this.key = new NamespacedKey(plugin, "favorites");
        this.plugin = plugin;
    }

    @Override
    public List<IWrap> get(Player source) {
        var pdc = source.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (pdc == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(pdc.split(SEPARATOR))
                .filter(uuid -> plugin.getWraps().containsKey(uuid)).map(uuid -> plugin.getWraps().get(uuid)).collect(Collectors.toList());
    }

    @Override
    public void set(Player source, List<IWrap> value) {
        var newValue = String.join(SEPARATOR, value.stream().map(IWrap::getUuid).toList());
        source.getPersistentDataContainer().set(key, PersistentDataType.STRING, newValue);
    }

}
