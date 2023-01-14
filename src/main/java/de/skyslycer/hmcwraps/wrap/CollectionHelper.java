package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.IWrappableItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class CollectionHelper implements ICollectionHelper {

    private final HMCWraps plugin;

    public CollectionHelper(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<IWrappableItem> getItems(Material material) {
        var list = new ArrayList<IWrappableItem>();
        if (plugin.getWrappableItems().containsKey(material.toString())) {
            list.add(plugin.getWrappableItems().get(material.toString()));
        }
        plugin.getCollections().entrySet().stream().filter(items -> items.getValue().contains(material.toString())).forEach(it -> {
            if (plugin.getWrappableItems().containsKey(it.getKey())) {
                list.add(plugin.getWrappableItems().get(it.getKey()));
            }
        });
        return list;
    }

    @Override
    public List<Material> getMaterials(String collection) {
        var list = new ArrayList<Material>();
        if (!plugin.getCollections().containsKey(collection)) {
            return List.of();
        }
        for (String materialName : plugin.getCollections().get(collection)) {
            if (Material.getMaterial(materialName) != null) {
                list.add(Material.getMaterial(materialName));
            }
        }
        return list;
    }

}
