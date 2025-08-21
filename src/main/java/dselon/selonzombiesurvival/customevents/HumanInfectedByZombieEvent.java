package dselon.selonzombiesurvival.customevents;

import org.bukkit.entity.Player;

public class HumanInfectedByZombieEvent extends HumanInfectedEvent{
    private Player zombie;

    public HumanInfectedByZombieEvent(Player human, Player zombie) {
        super(human);
        this.zombie = zombie;
    }

    public Player getZombie() {
        return zombie;
    }

    public void setZombie(Player zombie) {
        this.zombie = zombie;
    }
}
