package de.skyslycer.hmcwraps.actions.information;

import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;

public interface IGuiActionInformation extends ActionInformation {

    @Override
    Player getPlayer();

    @Override
    String getArguments();

    PaginatedGui getGui();

    @Override
    void setPlayer(Player player);

    @Override
    void setArguments(String arguments);

    void setGui(PaginatedGui gui);
}
