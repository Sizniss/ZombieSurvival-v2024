package dselon.selonzombiesurvival.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamChangeHostEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player host;

    public TeamChangeHostEvent(Player carrier) {
        this.host = carrier;
    }

    public Player getHost() {
        return host;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
