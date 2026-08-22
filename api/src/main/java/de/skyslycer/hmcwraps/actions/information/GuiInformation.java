package de.skyslycer.hmcwraps.actions.information;

import dev.triumphteam.gui.guis.PaginatedGui;

public interface GuiInformation extends ActionInformation {
    PaginatedGui getGui();
    int getSlot();
    String getCategory();
    void setSlot(int slot);
    void setGui(PaginatedGui gui);
    void setCategory(String category);
}
