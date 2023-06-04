package de.skyslycer.hmcwraps.actions.information;

import org.bukkit.entity.Player;

public class BasicActionInformation implements ActionInformation {

    private Player player;
    private String arguments;

    public BasicActionInformation(Player player, String arguments) {
        this.player = player;
        this.arguments = arguments;
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
    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

}
