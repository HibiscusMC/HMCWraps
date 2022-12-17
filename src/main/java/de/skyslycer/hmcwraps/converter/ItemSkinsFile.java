package de.skyslycer.hmcwraps.converter;

import java.util.List;
import javax.annotation.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ItemSkinsFile {

    private List<String> material;
    private int customModelData;
    private @Nullable String permission;
    private Item availableItem;
    private @Nullable Item unavailableItem;
    private @Nullable Item physicalItem;

    public List<String> getMaterial() { return material; }

    public int getCustomModelData() { return customModelData; }

    @Nullable
    public String getPermission() { return permission; }

    public Item getAvailableItem() { return availableItem; }

    @Nullable
    public Item getUnavailableItem() { return unavailableItem; }

    @Nullable
    public Item getPhysicalItem() { return physicalItem; }

    @ConfigSerializable
    public static class Item {

        private String material;
        private String displayName;
        private @Nullable Integer customModelData;
        private @Nullable Boolean glowing;
        private @Nullable List<String> lore;

        public String getMaterial() { return material; }

        public String getDisplayName() { return displayName; }

        @Nullable
        public Integer getCustomModelData() { return customModelData; }

        @Nullable
        public Boolean getGlowing() { return glowing; }

        @Nullable
        public List<String> getLore() { return lore; }

    }

}
