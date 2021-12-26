package ru.ckateptb.abilityslots.ability.conditional;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

public interface AbilityConditional {
    boolean matches(AbilityUser user, AbilityInformation ability);
}
