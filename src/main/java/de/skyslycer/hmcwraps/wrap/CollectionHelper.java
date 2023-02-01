package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.IWrap;
import de.skyslycer.hmcwraps.serialization.IWrappableItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
            if (Material.getMaterial(collection) != null) {
                return List.of(Material.getMaterial(collection));
            } else {
                return List.of();
            }
        }
        for (String materialName : plugin.getCollections().get(collection)) {
            if (Material.getMaterial(materialName) != null) {
                list.add(Material.getMaterial(materialName));
            }
        }
        return list;
    }

    @Override
    public Material getMaterial(IWrap wrap) {
        var currentCollection = "";
        var itemMaterial = Material.AIR;
        for (Map.Entry<String, IWrappableItem> entry : plugin.getWrappableItems().entrySet()) {
            currentCollection = entry.getKey();
            if (entry.getValue().getWraps().containsValue(wrap)) {
                break;
            }
        }

        if (Material.getMaterial(currentCollection) != null) {
            itemMaterial = Material.getMaterial(currentCollection);
        } else if (plugin.getCollectionHelper().getMaterials(currentCollection).stream().findFirst().isPresent()) {
            itemMaterial = plugin.getCollectionHelper().getMaterials(currentCollection).stream().findFirst().get();
        } else {
            return null;
        }
        return itemMaterial;
    }

}
