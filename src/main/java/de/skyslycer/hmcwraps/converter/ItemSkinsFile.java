package de.skyslycer.hmcwraps.converter;

import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class ItemSkinsFile {

    private List<String> material;
    private int customModelData;
    private @Nullable String permission;
    private Item availableItem;
    private @Nullable Item unavailableItem;
    private @Nullable Item physicalItem;

    public List<String> getMaterial() {
        return material;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    @Nullable
    public String getPermission() {
        return permission;
    }

    public Item getAvailableItem() {
        return availableItem;
    }

    @Nullable
    public Item getUnavailableItem() {
        return unavailableItem;
    }

    @Nullable
    public Item getPhysicalItem() {
        return physicalItem;
    }

    @ConfigSerializable
    public static class Item {

        private String material;
        private String displayName;
        private @Nullable Integer customModelData;
        private @Nullable Boolean glowing;
        private @Nullable List<String> lore;

        public String getMaterial() {
            return material;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Nullable
        public Integer getCustomModelData() {
            return customModelData;
        }

        @Nullable
        public Boolean getGlowing() {
            return glowing;
        }

        @Nullable
        public List<String> getLore() {
            return lore;
        }

        public SerializableItem toItem() {
            return new SerializableItem(material, displayName, glowing, lore, null, customModelData, null, 1, null, null, null);
        }

    }

}
