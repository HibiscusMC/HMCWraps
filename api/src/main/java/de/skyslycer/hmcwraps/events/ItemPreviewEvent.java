package de.skyslycer.hmcwraps.events;

import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemPreviewEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    boolean isCancelled = false;

    private Player player;
    private ItemStack item;
    private Consumer<Player> onClose;
    private final Wrap wrap;

    public ItemPreviewEvent(Player player, ItemStack item, Consumer<Player> onClose, Wrap wrap) {
        this.player = player;
        this.item = item;
        this.onClose = onClose;
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

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return item;
    }

    public Wrap getWrap() {
        return wrap;
    }

    public Consumer<Player> getOnClose() {
        return onClose;
    }

    public ItemPreviewEvent setPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ItemPreviewEvent setItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public ItemPreviewEvent setOnClose(Consumer<Player> onClose) {
        this.onClose = onClose;
        return this;
    }

}
