package de.skyslycer.hmcwraps.serialization.inventory;

import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;

public enum Action {

    SCROLL_FORTH,
    SCROLL_BACK,
    CLOSE;

    public static void add(GuiItem item, PaginatedGui gui, Action action) {
        switch (action) {
            case SCROLL_FORTH -> item.setAction(event -> gui.next());
            case SCROLL_BACK -> item.setAction(event -> gui.previous());
            case CLOSE -> item.setAction(event -> event.getWhoClicked().closeInventory());
        }
    }

}
