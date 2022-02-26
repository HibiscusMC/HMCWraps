package de.skyslycer.hmcwraps.serialization.item;

import de.skyslycer.hmcwraps.serialization.inventory.Action;
import de.skyslycer.hmcwraps.util.EnumUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class SerializableItem {

    private String id;
    private String name;
    private @Nullable List<String> lore;
    private @Nullable List<String> flags;
    private @Nullable Integer modellId;
    private @Nullable Map<String, Integer> enchantments;
    private @Nullable Integer amount;
    private @Nullable Action action;

    @Nullable
    public ItemStack toItem() {
        ItemStack origin;
        if (Material.getMaterial(id) == null) {
            return null;
        } else {
            origin = new ItemStack(Material.getMaterial(id)); // TODO: Add ItemsAdder & Oraxen
        }
        ItemBuilder builder = ItemBuilder.from(origin);
        builder.name(StringUtil.parse(getName())).amount(getAmount() == null ? 1 : getAmount());

        if (getLore() != null) {
            builder.lore(getLore().stream().map(StringUtil::parse).collect(Collectors.toList()));
        }
        if (getFlags() != null) {
            List<ItemFlag> parsed = EnumUtil.getAllPossibilities(getFlags(), ItemFlag.class);
            builder.flags(parsed.toArray(ItemFlag[]::new));
        }
        if (getModellId() != null) {
            builder.model(getModellId());
        }
        if (getEnchantments() != null) {
            getEnchantments().forEach((name, level) -> {
                Enchantment enchantment = Enchantment.getByKey(NamespacedKey.minecraft(name));
                if (enchantment != null) {
                    builder.enchant(enchantment, level);
                }
            });
        }
        return builder.build();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public List<String> getLore() {
        return lore;
    }

    @Nullable
    public List<String> getFlags() {
        return flags;
    }

    @Nullable
    public Integer getModellId() {
        return modellId;
    }

    @Nullable
    public Map<String, Integer> getEnchantments() {
        return enchantments;
    }

    @Nullable
    public Integer getAmount() {
        return amount;
    }

    @Nullable
    public Action getAction() {
        return action;
    }

}
