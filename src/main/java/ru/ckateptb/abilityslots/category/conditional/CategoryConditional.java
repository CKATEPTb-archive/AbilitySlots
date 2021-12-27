package ru.ckateptb.abilityslots.category.conditional;

import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.conditional.AbilityUserConditional;
import ru.ckateptb.abilityslots.user.AbilityUser;

public interface CategoryConditional extends AbilityUserConditional<AbilityCategory> {
    @Override
    boolean matches(AbilityUser user, AbilityCategory ability);
}
