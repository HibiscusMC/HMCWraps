package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.HMCWraps;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public enum Action {

    SCROLL_FORTH,
    SCROLL_BACK,
    UNWRAP,
    CLOSE;

    public static void add(GuiItem item, PaginatedGui gui, Action action, HMCWraps plugin) {
        switch (action) {
            case SCROLL_FORTH -> item.setAction(event -> gui.next());
            case SCROLL_BACK -> item.setAction(event -> gui.previous());
            case CLOSE -> item.setAction(event -> event.getWhoClicked().closeInventory());
            case UNWRAP -> item.setAction(event -> plugin.getWrapper().removeWrap(
                    event.getWhoClicked().getInventory().getItemInMainHand(), (Player) event.getWhoClicked())
            );
        }
    }

}
