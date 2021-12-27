package ru.ckateptb.abilityslots.conditional;

import ru.ckateptb.abilityslots.user.AbilityUser;

public interface AbilityUserConditional<T> {
    boolean matches(AbilityUser user, T object);
}
