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
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class WrapsLoaderImpl implements WrapsLoader {

    private final HMCWrapsPlugin plugin;

    private final Map<String, Wrap> wraps = new ConcurrentHashMap<>();
    private final Map<String, List<String>> collections = new ConcurrentHashMap<>();
    private final Map<String, WrappableItem> wrappableItems = new ConcurrentHashMap<>();
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
        wrappableItems.clear();
        wrapFiles.clear();
        collectionFiles.clear();
    }

    private void combineFiles() {
        collections.putAll(plugin.getConfiguration().getCollections());
        collectionFiles.stream().filter(Toggleable::isEnabled).forEach(collectionFile -> collections.putAll(collectionFile.getCollections()));

        wrappableItems.putAll(plugin.getConfiguration().getItems());
        wrapFiles.forEach(it -> it.getItems().forEach((type, wrappableItem) -> {
            if (wrappableItems.containsKey(type)) {
                var current = wrappableItems.get(type);
                wrappableItem.getWraps().values().forEach(wrap -> current.putWrap(String.valueOf(findNextId(current)), wrap));
                wrappableItems.put(type, current);
            } else {
                wrappableItems.put(type, wrappableItem);
            }
        }));
        wrappableItems.values().forEach(wrappableItem -> wrappableItem.getWraps().values().forEach(wrap -> wraps.put(wrap.getUuid(), wrap)));

        wraps.remove("-");
    }

    // This is janky, yes. But it works.
    private int findNextId(WrappableItem wrappableItem) {
        var id = wrappableItem.getWraps().size() + 1;
        var level = 0;
        while (wrappableItem.getWraps().containsKey(String.valueOf(id))) {
            level++;
            if (level < 10) {
                id = ThreadLocalRandom.current().nextInt();
            } else {
                id++;
            }
        }
        return id;
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
        return collections;
    }

    @Override
    @NotNull
    public Set<CollectionFile> getCollectionFiles() {
        return collectionFiles;
    }

    @Override
    @NotNull
    public Map<String, Wrap> getWraps() {
        return wraps;
    }

    @Override
    @NotNull
    public Set<WrapFile> getWrapFiles() {
        return wrapFiles;
    }

    @Override
    @NotNull
    public Map<String, WrappableItem> getWrappableItems() {
        return wrappableItems;
    }

}
