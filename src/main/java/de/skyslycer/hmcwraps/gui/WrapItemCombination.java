package de.skyslycer.hmcwraps.gui;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.inventory.ItemStack;

public class WrapItemCombination {

    private final Wrap wrap;
    private final ItemStack item;

    public WrapItemCombination(Wrap wrap, ItemStack item) {
        this.wrap = wrap;
        this.item = item;
    }

    public Wrap getWrap() {
        return wrap;
    }

    public ItemStack getItem() {
        return item;
    }

}
