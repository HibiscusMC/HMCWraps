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
                    var permissionCompare = Boolean.compare(wrap2.getWrap().hasPermission(player), wrap1.getWrap().hasPermission(player));
                    if (permissionCompare != 0) {
                        return permissionCompare;
                    }
                }
                case "SORT_ID" -> {
                    var sortId1 = wrap1.getWrap().getSort() != null ? wrap1.getWrap().getSort() : Integer.MAX_VALUE;
                    var sortId2 = wrap2.getWrap().getSort() != null ? wrap2.getWrap().getSort() : Integer.MAX_VALUE;
                    var sortCompare = Integer.compare(sortId1, sortId2);
                    if (sortCompare != 0) {
                        return sortCompare;
                    }
                }
                case "MODEL_ID" -> {
                    int modelData1 = wrap1.getItem().getItemMeta().hasCustomModelData() ? wrap1.getItem().getItemMeta().getCustomModelData() : Integer.MAX_VALUE;
                    int modelData2 = wrap2.getItem().getItemMeta().hasCustomModelData() ? wrap2.getItem().getItemMeta().getCustomModelData() : Integer.MAX_VALUE;
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

