package de.skyslycer.hmcwraps.actions.information;

import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public class GuiActionInformation extends BasicActionInformation implements GuiInformation {

    private PaginatedGui gui;
    private int slot;
    private String category;

    public GuiActionInformation(Player player, String arguments, PaginatedGui gui, int slot, String category) {
        super(player, arguments);
        this.gui = gui;
        this.slot = slot;
        this.category = category;
    }

    public PaginatedGui getGui() {
        return gui;
    }

    public int getSlot() {
        return slot;
    }

    public String getCategory() {
        return category;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public void setGui(PaginatedGui gui) {
        this.gui = gui;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
