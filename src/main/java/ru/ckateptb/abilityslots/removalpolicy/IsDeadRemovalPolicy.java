package ru.ckateptb.abilityslots.removalpolicy;

import org.bukkit.entity.Player;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

public class IsDeadRemovalPolicy implements RemovalPolicy {
    private final AbilityUser user;

    public IsDeadRemovalPolicy(AbilityUser user) {
        this.user = user;
    }

    @Override
    public boolean shouldRemove() {
        if (!(user instanceof PlayerAbilityUser playerAbilityUser)) {
            return user.getEntity().isDead();
        }

        Player player = playerAbilityUser.getEntity();

        return !player.isOnline() || player.isDead();
    }
}
