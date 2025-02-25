package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.Toggleable;
import de.skyslycer.hmcwraps.serialization.files.CollectionFile;
import de.skyslycer.hmcwraps.serialization.files.WrapFile;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.transformation.WrapFileTransformations;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class WrapsLoaderImpl implements WrapsLoader {

    private final HMCWrapsPlugin plugin;

    private final Map<String, Wrap> wraps = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> typeWraps = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> collections = new ConcurrentHashMap<>();
    private final Set<WrapFile> wrapFiles = new HashSet<>();
    private final Set<CollectionFile> collectionFiles = new HashSet<>();

    public WrapsLoaderImpl(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        loadCollectionFiles();
        loadWrapFiles();
        combineFiles();
    }

    @Override
    public void unload() {
        wraps.clear();
        collections.clear();
        wrapFiles.clear();
        typeWraps.clear();
        collectionFiles.clear();
    }

    private void combineFiles() {
        collections.putAll(listToSetMap(plugin.getConfiguration().getCollections()));
        collectionFiles.stream().filter(Toggleable::isEnabled).forEach(collectionFile -> collections.putAll(listToSetMap(collectionFile.getCollections())));
        addWraps(plugin.getConfiguration().getItems());
        wrapFiles.forEach(it -> addWraps(it.getItems()));
        wraps.remove("-");
    }

    private Map<String, Set<String>> listToSetMap(Map<String, List<String>> config) {
        Map<String, Set<String>> collections = new HashMap<>();
        config.forEach((collection, items) -> collections.put(collection, new HashSet<>(items)));
        return collections;
    }

    private Map<String, List<String>> setToListMap(Map<String, Set<String>> map) {
        Map<String, List<String>> listMap = new HashMap<>();
        map.forEach((key, value) -> listMap.put(key, new ArrayList<>(value)));
        return listMap;
    }

    private void addWraps(Map<String, WrappableItem> items) {
        items.forEach((type, wrappableItem) -> {
            wrappableItem.getWraps().values().forEach(wrap -> {
                if (wrap.getUuid() == null) {
                    plugin.getLogger().warning("A wrap with the material/collection '" + type + "' doesn't have a " +
                            "UUID assigned! Make sure every wrap has a UUID assigned and check for typos like 'uid' instead of 'uuid'.");
                    return;
                }
                if (wraps.containsKey(wrap.getUuid())) {
                    plugin.getLogger().warning("A wrap with the UUID " + wrap.getUuid() + " already exists! Skipping the wrap for type " + type + ". Please check your configuration files for duplicates.");
                    return;
                }
                wraps.put(wrap.getUuid(), wrap);
                if (typeWraps.containsKey(type)) {
                    var current = typeWraps.get(type);
                    current.add(wrap.getUuid());
                    typeWraps.put(type, current);
                } else {
                    typeWraps.put(type, new HashSet<>(List.of(wrap.getUuid())));
                }
            });
        });
    }

    private void loadWrapFiles() {
        try (Stream<Path> paths = Files.find(HMCWraps.WRAP_FILES_PATH, 10,
                ((path, attributes) -> attributes.isRegularFile() && (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))))) {
            paths.forEach(path -> {
                try {
                    var loader = YamlConfigurationLoader.builder()
                            .defaultOptions(ConfigurationOptions.defaults().implicitInitialization(false))
                            .path(path)
                            .build();
                    new WrapFileTransformations().updateToLatest(path);
                    var wrapFile = loader.load().get(WrapFile.class);
                    if (wrapFile != null && wrapFile.isEnabled()) {
                        wrapFiles.add(wrapFile);
                    }
                } catch (IOException exception) {
                    plugin.logSevere(
                            "Could not load the wrap file " + path.getFileName().toString() + " (please report this to the developers)!", exception);
                }
            });
        } catch (IOException exception) {
            plugin.logSevere("Could not find the wrap files (please report this to the developers)!", exception);
        }
    }

    private void loadCollectionFiles() {
        try (Stream<Path> paths = Files.find(HMCWraps.COLLECTION_FILES_PATH, 10,
                ((path, attributes) -> attributes.isRegularFile() && (path.toString().endsWith(".yml") || path.toString().endsWith(".yaml"))))) {
            paths.forEach(path -> {
                try {
                    var collectionFile = YamlConfigurationLoader.builder()
                            .defaultOptions(ConfigurationOptions.defaults().implicitInitialization(false))
                            .path(path)
                            .build().load().get(CollectionFile.class);
                    if (collectionFile != null && collectionFile.isEnabled()) {
                        collectionFiles.add(collectionFile);
                    }
                } catch (ConfigurateException exception) {
                    plugin.logSevere(
                            "Could not load the collection file " + path.getFileName().toString() + " (please report this to the developers)!", exception);
                }
            });
        } catch (IOException exception) {
            plugin.logSevere("Could not find the wrap files (please report this to the developers)!", exception);
        }
    }

    @Override
    @NotNull
    public Map<String, List<String>> getCollections() {
        return setToListMap(collections);
    }

    @Override
    public int getCollectionFileCount() {
        return collectionFiles.size();
    }

    @Override
    public int getWrapFileCount() {
        return wrapFiles.size();
    }

    @Override
    @NotNull
    public Map<String, Wrap> getWraps() {
        return wraps;
    }

    @Override
    @NotNull
    public Map<String, List<String>> getTypeWraps() {
        return setToListMap(typeWraps);
    }

}
