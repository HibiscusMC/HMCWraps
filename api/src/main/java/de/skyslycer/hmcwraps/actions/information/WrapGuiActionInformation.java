package de.skyslycer.hmcwraps.actions.information;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public class WrapGuiActionInformation extends GuiActionInformation implements WrapInformation {

    private Wrap wrap;

    public WrapGuiActionInformation(PaginatedGui gui, Wrap wrap, Player player, int slot, String category, String arguments) {
        super(player, arguments, gui, slot, category);
        this.wrap = wrap;
    }

    public Wrap getWrap() {
        return wrap;
    }

    public void setWrap(Wrap wrap) {
        this.wrap = wrap;
    }

}
