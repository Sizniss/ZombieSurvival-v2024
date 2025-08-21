package dselon.selonzombiesurvival.customevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ZombieWinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public ZombieWinEvent() {

    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
