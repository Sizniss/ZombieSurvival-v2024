package dselon.selonzombiesurvival.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ZombieKilledEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player zombie;

    public ZombieKilledEvent(Player zombie) {
        this.zombie = zombie;
    }

    public Player getZombie() {
        return zombie;
    }

    public void setZombie(Player zombie) {
        this.zombie = zombie;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
