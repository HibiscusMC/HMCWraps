package de.skyslycer.hmcwraps.gui;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.inventory.Inventory;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemComparator implements Comparator<WrapItemCombination> {

    private final Inventory inventorySettings;
    private final Player player;
    private final Set<String> favoriteWraps;

    public ItemComparator(HMCWrapsPlugin plugin, Inventory inventorySettings, Player player) {
        this.inventorySettings = inventorySettings;
        this.player = player;
        this.favoriteWraps = plugin.getFavoriteWrapStorage().get(player).stream().map(Wrap::getUuid).collect(Collectors.toSet());
    }

    @Override
    public int compare(WrapItemCombination wrap1, WrapItemCombination wrap2) {
        for (String sortType : inventorySettings.getSortOrder()) {
            switch (sortType.toUpperCase()) {
                case "FAVORITE" -> {
                    var favoriteCompare = Boolean.compare(favoriteWraps.contains(wrap2.wrap().getUuid()), favoriteWraps.contains(wrap1.wrap().getUuid()));
                    if (favoriteCompare != 0) {
                        return favoriteCompare;
                    }
                }
                case "PERMISSION" -> {
                    var permissionCompare = Boolean.compare(wrap2.permission(), wrap1.permission());
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
                    int modelData1 = wrap1.modelId() >= 0 ? wrap1.modelId() : Integer.MAX_VALUE;
                    int modelData2 = wrap2.modelId() >= 0 ? wrap2.modelId() : Integer.MAX_VALUE;
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
