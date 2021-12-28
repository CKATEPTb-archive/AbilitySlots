package ru.ckateptb.abilityslots.removalpolicy;

import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

public class SneakingRemovalPolicy implements RemovalPolicy {
    private final AbilityUser user;
    private final boolean shouldSneak;

    public SneakingRemovalPolicy(AbilityUser user, boolean shouldSneak) {
        this.user = user;
        this.shouldSneak = shouldSneak;
    }

    @Override
    public boolean shouldRemove() {
        return user instanceof PlayerAbilityUser playerAbilityUser && playerAbilityUser.getEntity().isSneaking() != shouldSneak;
    }
}
