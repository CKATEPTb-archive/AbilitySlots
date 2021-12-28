package ru.ckateptb.abilityslots.removalpolicy;

import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

public class IsOfflineRemovalPolicy implements RemovalPolicy {
    private final AbilityUser user;

    public IsOfflineRemovalPolicy(AbilityUser user) {
        this.user = user;
    }

    @Override
    public boolean shouldRemove() {
        if (!(user instanceof PlayerAbilityUser playerAbilityUser)) return false;

        return !playerAbilityUser.getEntity().isOnline();
    }
}
