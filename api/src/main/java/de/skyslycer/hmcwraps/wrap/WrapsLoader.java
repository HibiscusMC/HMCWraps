package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.serialization.files.CollectionFile;
import de.skyslycer.hmcwraps.serialization.files.WrapFile;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WrapsLoader {

    /**
     * Load all collection files and wrap files.
     */
    void load();

    /**
     * Unload all collection files and wrap files.
     */
    void unload();

    /**
     * Get all currently loaded collections.
     *
     * @return All currently loaded collections
     */
    Map<String, List<String>> getCollections();

    /**
     * Get all currently loaded collection files.
     *
     * @return All collection files
     */
    Set<CollectionFile> getCollectionFiles();

    /**
     * All wraps currently configured.
     *
     * @return All wraps
     */
    Map<String, Wrap> getWraps();

    /**
     * All wrap files currently loaded.
     * NOTE: This also contains disabled wrap files, you have to filter for enabled ones yourself.
     *
     * @return All currently loaded wrap files
     */
    Set<WrapFile> getWrapFiles();

    /**
     * All currently loaded wrappable items.
     * This map contains all wraps that can be applied to a particular material/collection.
     *
     * @return All currently loaded wrappable items
     */
    Map<String, WrappableItem> getWrappableItems();

}
