package de.skyslycer.hmcwraps.wrap;


import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import org.bukkit.Material;

import java.util.List;

public interface CollectionHelper {

    /**
     * Get all wraps for a material.
     *
     * @param material The material to search for
     * @return A list of all wraps
     */
    List<WrappableItem> getItems(Material material);

    /**
     * Get all materials in a collection.
     *
     * @param collection The collection
     * @return All materials in the collection
     */
    List<Material> getMaterials(String collection);

    /**
     * Get a possible material from a wrap.
     *
     * @param wrap The wrap to get the material from
     * @return One possible material
     */
    Material getMaterial(Wrap wrap);

    /**
     * Get the collection or material name of a wrap.
     *
     * @param wrap The wrap to get the collection from
     * @return The collection or material name
     */
    String getCollection(Wrap wrap);

}
