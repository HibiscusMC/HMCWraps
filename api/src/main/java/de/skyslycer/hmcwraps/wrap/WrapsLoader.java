package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;

import java.util.List;
import java.util.Map;

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
     * The amount of currently loaded collection files, this includes disabled ones.
     *
     * @return The amount of currently loaded collection files
     */
    int getCollectionFileCount();

    /**
     * The amount of currently loaded wrap files, this includes disabled ones.
     *
     * @return The amount of currently loaded wrap files
     */
    int getWrapFileCount();

    /**
     * All wraps currently configured.
     *
     * @return All wraps
     */
    Map<String, Wrap> getWraps();

    /**
     * All wrap UUIDs for a certain material or collection.
     *
     * @return All wrap UUIDs mapped to a material or collection
     */
    Map<String, List<String>> getTypeWraps();

}
