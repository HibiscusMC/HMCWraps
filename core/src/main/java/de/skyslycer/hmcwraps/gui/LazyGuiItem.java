package de.skyslycer.hmcwraps.gui;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Supplier;

/**
 * A GUI item that builds its ItemStack the first time it is shown. A paginated GUI only asks for the
 * items of the open page, so opening the wraps menu no longer builds every wrap at once.
 */
public class LazyGuiItem extends GuiItem {

    private Supplier<ItemStack> supplier;

    public LazyGuiItem(Supplier<ItemStack> supplier) {
        super(new ItemStack(Material.STONE));
        this.supplier = supplier;
    }

    @Override
    public ItemStack getItemStack() {
        Supplier<ItemStack> pending = supplier;
        if (pending != null) {
            supplier = null;
            setItemStack(pending.get());
        }
        return super.getItemStack();
    }
}
