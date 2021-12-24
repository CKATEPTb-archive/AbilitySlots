package ru.ckateptb.abilityslots.ability.conditional;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

public class EnabledAbilityActivateConditional implements AbilityActivateConditional {
    @Override
    public boolean canActivate(AbilityUser user, AbilityInformation ability) {
        return ability != null && ability.isEnabled();
    }
}
