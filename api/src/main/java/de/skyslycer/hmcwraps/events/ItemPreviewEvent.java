package de.skyslycer.hmcwraps.events;

import de.skyslycer.hmcwraps.serialization.IWrap;
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
    private final IWrap wrap;

    public ItemPreviewEvent(Player player, ItemStack item, PaginatedGui gui, IWrap wrap) {
        this.player = player;
        this.item = item;
        this.gui = gui;
        this.wrap = wrap;
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

    public IWrap getWrap() {
        return wrap;
    }

    public ItemPreviewEvent setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ItemPreviewEvent setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public ItemPreviewEvent setGui(PaginatedGui gui) {
        this.gui = gui;
        return this;
    }

}
