package ru.ckateptb.abilityslots.category.conditional;

import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

public class PermissionCategoryConditional implements CategoryConditional {
    @Override
    public boolean matches(AbilityUser user, AbilityCategory category) {
        if (category == null) return false;
        if (user instanceof PlayerAbilityUser playerAbilityUser) {
            String permission = String.format("abilityslots.abilities.%s", category.getName()).toLowerCase();
            return playerAbilityUser.getEntity().hasPermission(permission);
        }
        return true;
    }
}
