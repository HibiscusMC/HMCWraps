package de.skyslycer.hmcwraps.actions.information;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public class WrapGuiActionInformation implements ActionInformation {

    private PaginatedGui gui;
    private Wrap wrap;
    private Player player;
    private String arguments;
    private int slot;

    public WrapGuiActionInformation(PaginatedGui gui, Wrap wrap, Player player, int slot, String arguments) {
        this.gui = gui;
        this.wrap = wrap;
        this.player = player;
        this.arguments = arguments;
        this.slot = slot;
    }

    public PaginatedGui getGui() {
        return gui;
    }

    public void setGui(PaginatedGui gui) {
        this.gui = gui;
    }

    public Wrap getWrap() {
        return wrap;
    }

    public void setWrap(Wrap wrap) {
        this.wrap = wrap;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public String getArguments() {
        return arguments;
    }

    @Override
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

}
