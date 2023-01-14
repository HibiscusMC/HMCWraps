package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.serialization.IWrap;
import de.skyslycer.hmcwraps.serialization.IWrappableItem;
import org.bukkit.Material;

import java.util.List;

public interface ICollectionHelper {

    /**
     * Get all wraps for a material.
     *
     * @param material The material to search for
     * @return A list of all wraps
     */
    List<IWrappableItem> getItems(Material material);

    /**
     * Get all materials in a collection.
     *
     * @param collection The collection
     * @return All materials in the collection
     */
    List<Material> getMaterials(String collection);

    /**
     * Get a possible material from a wrap.
     * @param wrap The wrap to get the material from
     * @return One possible material
     */
    Material getMaterial(IWrap wrap);

}
