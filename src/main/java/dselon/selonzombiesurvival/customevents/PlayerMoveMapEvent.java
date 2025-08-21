package dselon.selonzombiesurvival.customevents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class PlayerMoveMapEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private Player player;
    private ArrayList<Location> mapLocation;

    public PlayerMoveMapEvent(Player player, ArrayList<Location> mapLocation) {
        this.player = player;
        this.mapLocation = mapLocation;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ArrayList<Location> getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(ArrayList<Location> mapLocation) {
        this.mapLocation = mapLocation;
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
