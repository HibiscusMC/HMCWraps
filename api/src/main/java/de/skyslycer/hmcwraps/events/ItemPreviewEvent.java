package de.skyslycer.hmcwraps.events;

import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemPreviewEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    boolean isCancelled = false;

    private Player player;
    private ItemStack item;
    private PaginatedGui gui;

    public ItemPreviewEvent(Player player, ItemStack item, PaginatedGui gui) {
        this.item = item;
        this.gui = gui;
        this.player = player;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public PaginatedGui getGui() {
        return gui;
    }

    public ItemPreviewEvent setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ItemPreviewEvent getItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public ItemPreviewEvent setGui(PaginatedGui gui) {
        this.gui = gui;
        return this;
    }

}
