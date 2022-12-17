package de.skyslycer.hmcwraps.actions.information;

import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public class GuiActionInformation implements IGuiActionInformation {

    Player player;
    String arguments;
    PaginatedGui gui;

    public GuiActionInformation(Player player, String arguments, PaginatedGui gui) {
        this.player = player;
        this.arguments = arguments;
        this.gui = gui;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getArguments() {
        return arguments;
    }

    @Override
    public PaginatedGui getGui() {
        return gui;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void setGui(PaginatedGui gui) {
        this.gui = gui;
    }

}
