package ru.ckateptb.abilityslots.ability;

import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.enums.UpdateResult;
import ru.ckateptb.abilityslots.user.AbilityUser;

public interface Ability {
    boolean activate(AbilityUser user, ActivationMethod method);

    UpdateResult update();

    void destroy();
}
