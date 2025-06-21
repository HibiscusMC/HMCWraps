package de.skyslycer.hmcwraps.gui;

import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import org.bukkit.entity.Player;

import java.util.Comparator;

public class ItemComparator implements Comparator<WrapItemCombination> {

    private final Inventory inventorySettings;
    private final Player player;

    public ItemComparator(Inventory inventorySettings, Player player) {
        this.inventorySettings = inventorySettings;
        this.player = player;
    }

    @Override
    public int compare(WrapItemCombination wrap1, WrapItemCombination wrap2) {
        for (String sortType : inventorySettings.getSortOrder()) {
            switch (sortType.toUpperCase()) {
                case "PERMISSION" -> {
                    var permissionCompare = Boolean.compare(wrap2.wrap().hasPermission(player), wrap1.wrap().hasPermission(player));
                    if (permissionCompare != 0) {
                        return permissionCompare;
                    }
                }
                case "SORT_ID" -> {
                    var sortId1 = wrap1.wrap().getSort() != null ? wrap1.wrap().getSort() : Integer.MAX_VALUE;
                    var sortId2 = wrap2.wrap().getSort() != null ? wrap2.wrap().getSort() : Integer.MAX_VALUE;
                    var sortCompare = Integer.compare(sortId1, sortId2);
                    if (sortCompare != 0) {
                        return sortCompare;
                    }
                }
                case "MODEL_ID" -> {
                    int modelData1 = wrap1.item().getItemMeta().hasCustomModelData() ? wrap1.item().getItemMeta().getCustomModelData() : Integer.MAX_VALUE;
                    int modelData2 = wrap2.item().getItemMeta().hasCustomModelData() ? wrap2.item().getItemMeta().getCustomModelData() : Integer.MAX_VALUE;
                    var modelComapre = Integer.compare(modelData1, modelData2);
                    if (modelComapre != 0) {
                        return modelComapre;
                    }
                }
            }
        }
        return 0;
    }

}

