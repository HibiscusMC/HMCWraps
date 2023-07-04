package de.skyslycer.hmcwraps.actions.information;

import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public class GuiActionInformation implements ActionInformation {

    private PaginatedGui gui;
    private Player player;
    private String arguments;
    private int slot;

    public GuiActionInformation(Player player, String arguments, PaginatedGui gui, int slot) {
        this.player = player;
        this.arguments = arguments;
        this.gui = gui;
        this.slot = slot;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public String getArguments() {
        return arguments;
    }

    public PaginatedGui getGui() {
        return gui;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public void setGui(PaginatedGui gui) {
        this.gui = gui;
    }

}
