package dselon.selonzombiesurvival.customevents;

import org.bukkit.entity.Player;

public class ZombieKilledByHumanEvent extends ZombieKilledEvent {
    private Player human;

    public ZombieKilledByHumanEvent(Player zombie, Player human) {
        super(zombie);
        this.human = human;
    }

    public Player getHuman() {
        return human;
    }

    public void setHuman(Player human) {
        this.human = human;
    }
}
