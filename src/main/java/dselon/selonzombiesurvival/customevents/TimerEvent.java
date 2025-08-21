package dselon.selonzombiesurvival.customevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TimerEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private int timer;

    public TimerEvent(int timer) {
        this.timer = timer;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
