package ru.ckateptb.abilityslots.removalpolicy;

import ru.ckateptb.abilityslots.ability.conditional.AbilityConditional;
import ru.ckateptb.abilityslots.ability.conditional.CompositeAbilityConditional;
import ru.ckateptb.abilityslots.ability.conditional.CooldownAbilityConditional;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.ArrayList;
import java.util.List;

public class CannotUseRemovalPolicy implements RemovalPolicy {
    private final AbilityUser user;
    private final boolean ignoreCooldowns;
    private final AbilityInformation abilityInformation;

    public CannotUseRemovalPolicy(AbilityUser user, AbilityInformation abilityDesc) {
        this(user, abilityDesc, false);
    }

    public CannotUseRemovalPolicy(AbilityUser user, AbilityInformation abilityInformation, boolean ignoreCooldowns) {
        this.user = user;
        this.abilityInformation = abilityInformation;
        this.ignoreCooldowns = ignoreCooldowns;
    }

    @Override
    public boolean shouldRemove() {
        CompositeAbilityConditional cond = user.getAbilityActivateConditional();

        List<AbilityConditional> removed = new ArrayList<>();

        if (ignoreCooldowns) {
            // Remove the cooldown check and add it again after.
            removed = cond.removeType(CooldownAbilityConditional.class);
        }

        boolean result = user.canActivate(abilityInformation);

        cond.add(removed);

        return !result;
    }
}
