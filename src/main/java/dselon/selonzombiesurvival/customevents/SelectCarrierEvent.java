package dselon.selonzombiesurvival.customevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;

public class SelectCarrierEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private ArrayList<Player> humanList;

    public SelectCarrierEvent(ArrayList<Player> humanList) {
        this.humanList = humanList;
    }

    public ArrayList<Player> getHumanList() {
        return humanList;
    }
    public void setHumanList(ArrayList<Player> humanList) {
        this.humanList = humanList;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}

