package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.serialization.IWrappableItem;
import java.util.List;
import org.bukkit.Material;

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

}
