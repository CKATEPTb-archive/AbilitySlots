package ru.ckateptb.abilityslots.ability.conditional;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

public class CategoryAbilityConditional implements AbilityConditional {
    @Override
    public boolean matches(AbilityUser user, AbilityInformation ability) {
        if (ability == null) return false;
        if (user instanceof PlayerAbilityUser playerAbilityUser) {
            String permission = String.format("abilityslots.abilities.%s", ability.getCategory().getName()).toLowerCase();
            return playerAbilityUser.getEntity().hasPermission(permission);
        }
        return true;
    }
}
