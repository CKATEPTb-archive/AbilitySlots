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
