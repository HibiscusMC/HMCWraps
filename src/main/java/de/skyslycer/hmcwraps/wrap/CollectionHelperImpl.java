package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionHelperImpl implements CollectionHelper {

    private final HMCWrapsPlugin plugin;

    public CollectionHelperImpl(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<WrappableItem> getItems(Material material) {
        var list = new ArrayList<WrappableItem>();
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
        if (!plugin.getCollections().containsKey(collection)) {
            if (Material.getMaterial(collection) != null) {
                return List.of(Material.getMaterial(collection));
            } else {
                return List.of();
            }
        }
        var list = new ArrayList<Material>();
        for (String materialName : plugin.getCollections().get(collection)) {
            if (Material.getMaterial(materialName) != null) {
                list.add(Material.getMaterial(materialName));
            }
        }
        return list;
    }

    @Override
    public Material getMaterial(Wrap wrap) {
        var currentCollection = getCollection(wrap);
        var itemMaterial = Material.AIR;
        if (Material.getMaterial(currentCollection) != null) {
            itemMaterial = Material.getMaterial(currentCollection);
        } else if (plugin.getCollectionHelper().getMaterials(currentCollection).stream().findFirst().isPresent()) {
            itemMaterial = plugin.getCollectionHelper().getMaterials(currentCollection).stream().findFirst().get();
        } else {
            return null;
        }
        return itemMaterial;
    }

    @Override
    public String getCollection(Wrap wrap) {
        var currentCollection = "";
        for (Map.Entry<String, WrappableItem> entry : plugin.getWrappableItems().entrySet()) {
            currentCollection = entry.getKey();
            if (entry.getValue().getWraps().containsValue(wrap)) {
                break;
            }
        }
        return currentCollection;
    }

}
