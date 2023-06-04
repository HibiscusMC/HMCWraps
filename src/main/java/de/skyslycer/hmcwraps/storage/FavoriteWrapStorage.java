package de.skyslycer.hmcwraps.storage;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FavoriteWrapStorage implements Storage<Player, List<Wrap>> {

    private static final String SEPARATOR = ";!;";

    private final NamespacedKey key;
    private final HMCWrapsPlugin plugin;

    public FavoriteWrapStorage(HMCWrapsPlugin plugin) {
        this.key = new NamespacedKey(plugin, "favorites");
        this.plugin = plugin;
    }

    @Override
    public List<Wrap> get(Player source) {
        var pdc = source.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        if (pdc == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(pdc.split(SEPARATOR))
                .filter(uuid -> plugin.getWrapsLoader().getWraps().containsKey(uuid)).map(uuid -> plugin.getWrapsLoader().getWraps().get(uuid)).collect(Collectors.toList());
    }

    @Override
    public void set(Player source, List<Wrap> value) {
        var newValue = String.join(SEPARATOR, value.stream().map(Wrap::getUuid).toList());
        source.getPersistentDataContainer().set(key, PersistentDataType.STRING, newValue);
    }

}
