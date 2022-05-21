package de.skyslycer.hmcwraps.serialization.inventory;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public enum Action {

    SCROLL_FORTH,
    SCROLL_BACK,
    NEXT_PAGE,
    PREVIOUS_PAGE,
    UNWRAP,
    CLOSE;

    public static void add(GuiItem item, PaginatedGui gui, Action action, HMCWraps plugin) {
        switch (action) {
            case SCROLL_FORTH -> item.setAction(event -> gui.next());
            case SCROLL_BACK -> item.setAction(event -> gui.previous());
            case NEXT_PAGE -> item.setAction(event -> gui.next());
            case PREVIOUS_PAGE -> item.setAction(event -> gui.previous());
            case CLOSE -> item.setAction(event -> event.getWhoClicked().closeInventory());
            case UNWRAP -> item.setAction(event -> {
                event.getWhoClicked().getInventory().setItemInMainHand(plugin.getWrapper().removeWrap(
                        event.getWhoClicked().getInventory().getItemInMainHand(), (Player) event.getWhoClicked(), true));
                event.getWhoClicked().getOpenInventory().close();
                plugin.getHandler().send(event.getWhoClicked(), Messages.REMOVE_WRAP);
            });
        }
    }

}
