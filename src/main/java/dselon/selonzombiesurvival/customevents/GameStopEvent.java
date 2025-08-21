package dselon.selonzombiesurvival.customevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameStopEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public GameStopEvent() {

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
