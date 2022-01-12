/*
 * Copyright (c) 2022 CKATEPTb <https://github.com/CKATEPTb>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
