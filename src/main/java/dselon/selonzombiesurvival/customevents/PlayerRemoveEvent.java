package dselon.selonzombiesurvival.customevents;

import dselon.selonzombiesurvival.Manager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRemoveEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private Manager.GameProgress gameProgress;

    public PlayerRemoveEvent(Player player, Manager.GameProgress gameProgress) {
        this.player = player;
        this.gameProgress = gameProgress;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Manager.GameProgress getGameProgress() {
        return gameProgress;
    }

    public void setGameProgress(Manager.GameProgress gameProgress) {
        this.gameProgress = gameProgress;
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
