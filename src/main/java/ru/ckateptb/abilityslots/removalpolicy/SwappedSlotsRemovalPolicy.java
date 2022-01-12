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
