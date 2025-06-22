package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.Material;

import java.util.*;

public class CollectionHelperImpl implements CollectionHelper {

    private final HMCWrapsPlugin plugin;

    public CollectionHelperImpl(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<Wrap> getItems(Material material) {
        var list = new HashSet<String>();
        if (plugin.getWrapsLoader().getTypeWraps().containsKey(material.toString())) {
            list.addAll(plugin.getWrapsLoader().getTypeWraps().get(material.toString()));
        }
        plugin.getWrapsLoader().getCollections().entrySet().stream().filter(items -> items.getValue().contains(material.toString())).forEach(it -> {
            if (plugin.getWrapsLoader().getTypeWraps().containsKey(it.getKey())) {
                list.addAll(plugin.getWrapsLoader().getTypeWraps().get(it.getKey()));
            }
        });
        return list.stream().map(plugin.getWrapsLoader().getWraps()::get).filter(Objects::nonNull).toList();
    }

    @Override
    public List<Material> getMaterials(String collection) {
        if (!plugin.getWrapsLoader().getCollections().containsKey(collection)) {
            if (Material.getMaterial(collection) != null) {
                return List.of(Material.getMaterial(collection));
            } else {
                return List.of();
            }
        }
        var list = new ArrayList<Material>();
        for (String materialName : plugin.getWrapsLoader().getCollections().get(collection)) {
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
        return plugin.getWrapsLoader().getTypeWraps().entrySet().stream().filter(it -> it.getValue().contains(wrap.getUuid()))
                .findFirst().map(Map.Entry::getKey).orElse(null);
    }

}
