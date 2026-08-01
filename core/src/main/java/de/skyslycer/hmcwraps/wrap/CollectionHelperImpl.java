package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.Material;

import java.util.*;

public class CollectionHelperImpl implements CollectionHelper {

    private final HMCWrapsPlugin plugin;
    private volatile Map<Material, List<Wrap>> itemsByMaterial = Map.of();

    public CollectionHelperImpl(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<Wrap> getItems(Material material) {
        return itemsByMaterial.getOrDefault(material, List.of());
    }

    public void refresh() {
        var loader = plugin.getWrapsLoader();
        var typeWraps = loader.getTypeWraps();
        var collections = loader.getCollections();
        var wraps = loader.getWraps();
        var wrapIdsByMaterial = new EnumMap<Material, Set<String>>(Material.class);

        for (var entry : typeWraps.entrySet()) {
            var material = Material.getMaterial(entry.getKey());
            if (material != null) {
                addWrapIds(wrapIdsByMaterial, material, entry.getValue());
            }
            var collection = collections.get(entry.getKey());
            if (collection == null) {
                continue;
            }
            for (String materialName : collection) {
                var collectionMaterial = Material.getMaterial(materialName);
                if (collectionMaterial != null) {
                    addWrapIds(wrapIdsByMaterial, collectionMaterial, entry.getValue());
                }
            }
        }

        var snapshot = new EnumMap<Material, List<Wrap>>(Material.class);
        for (var entry : wrapIdsByMaterial.entrySet()) {
            var materialWraps = new ArrayList<Wrap>();
            for (String wrapId : entry.getValue()) {
                var wrap = wraps.get(wrapId);
                if (wrap != null) {
                    materialWraps.add(wrap);
                }
            }
            snapshot.put(entry.getKey(), List.copyOf(materialWraps));
        }
        itemsByMaterial = Map.copyOf(snapshot);
    }

    public void clear() {
        itemsByMaterial = Map.of();
    }

    private void addWrapIds(Map<Material, Set<String>> wrapIdsByMaterial, Material material, Collection<String> wrapIds) {
        wrapIdsByMaterial.computeIfAbsent(material, ignored -> new HashSet<>()).addAll(wrapIds);
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
