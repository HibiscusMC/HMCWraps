package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.WrappableItem;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

public class CollectionHelper {

    private final HMCWraps plugin;

    public CollectionHelper(HMCWraps plugin) {
        this.plugin = plugin;
    }

    public List<WrappableItem> getItems(Material material) {
        var configuration = plugin.getConfiguration();
        var list = new ArrayList<WrappableItem>();
        if (configuration.getItems().containsKey(material.toString())) {
            list.add(configuration.getItems().get(material.toString()));
        }
        configuration.getCollections().entrySet().stream().filter(items -> items.getValue().contains(material.toString())).forEach(it -> {
            if (configuration.getItems().containsKey(it.getKey())) {
                list.add(configuration.getItems().get(it.getKey()));
            }
        });
        return list;
    }

    public List<Material> getMaterials(String collection) {
        var list = new ArrayList<Material>();
        if (!plugin.getConfiguration().getCollections().containsKey(collection)) {
            return List.of();
        }
        for (String materialName : plugin.getConfiguration().getCollections().get(collection)) {
            if (Material.getMaterial(materialName) != null) {
                list.add(Material.getMaterial(materialName));
            }
        }
        return list;
    }

}
