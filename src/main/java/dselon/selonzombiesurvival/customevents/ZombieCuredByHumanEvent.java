package dselon.selonzombiesurvival.customevents;

import org.bukkit.entity.Player;

public class ZombieCuredByHumanEvent extends ZombieCuredEvent{
    private Player human;

    public ZombieCuredByHumanEvent(Player zombie, Player human) {
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
