package dselon.selonzombiesurvival.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InfecteeChangeCarrierEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player carrier;

    public InfecteeChangeCarrierEvent(Player carrier) {
        this.carrier = carrier;
    }

    public Player getCarrier() {
        return carrier;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
