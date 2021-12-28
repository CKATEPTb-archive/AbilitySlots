package ru.ckateptb.abilityslots.removalpolicy;


import org.bukkit.World;
import ru.ckateptb.abilityslots.user.AbilityUser;

public class OutOfWorldRemovalPolicy implements RemovalPolicy {
    private final AbilityUser user;
    private final World world;

    public OutOfWorldRemovalPolicy(AbilityUser user) {
        this.user = user;
        this.world = user.getEntity().getWorld();
    }

    @Override
    public boolean shouldRemove() {
        return !user.getEntity().getWorld().equals(world);
    }
}
