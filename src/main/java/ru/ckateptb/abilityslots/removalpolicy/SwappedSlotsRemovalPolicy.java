package ru.ckateptb.abilityslots.removalpolicy;

import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

public class SwappedSlotsRemovalPolicy<T extends Ability> implements RemovalPolicy {
    private final AbilityUser user;
    private final Class<? extends T> type;

    public SwappedSlotsRemovalPolicy(AbilityUser user, Class<? extends T> type) {
        this.user = user;
        this.type = type;
    }

    @Override
    public boolean shouldRemove() {
        if (!(user instanceof PlayerAbilityUser)) return false;
        AbilityInformation information = user.getSelectedAbility();
        return information == null || !information.getAbilityClass().equals(type);
    }
}
