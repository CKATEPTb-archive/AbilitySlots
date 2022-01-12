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

package ru.ckateptb.abilityslots.ability.conditional;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeAbilityConditional implements AbilityConditional {
    private final List<AbilityConditional> conditionals = new ArrayList<>();

    @Override
    public boolean matches(AbilityUser user, AbilityInformation ability) {
        return conditionals.stream().allMatch((cond) -> cond.matches(user, ability));
    }

    public void add(AbilityConditional... conditionals) {
        this.conditionals.addAll(Arrays.asList(conditionals));
    }

    public void add(Collection<AbilityConditional> conditionals) {
        this.conditionals.addAll(conditionals);
    }

    public void add(AbilityConditional conditional) {
        conditionals.add(conditional);
    }

    public void remove(AbilityConditional conditional) {
        conditionals.remove(conditional);
    }

    public boolean hasType(Class<? extends AbilityConditional> type) {
        return conditionals.stream().anyMatch(cond -> type.isAssignableFrom(cond.getClass()));
    }

    // Returns all of the conditionals that were removed.
    public List<AbilityConditional> removeType(Class<? extends AbilityConditional> type) {
        // Filter out any conditionals that match the provided type.
        List<AbilityConditional> filtered = conditionals.stream()
                .filter((cond) -> type.isAssignableFrom(cond.getClass()))
                .collect(Collectors.toList());
        conditionals.removeAll(filtered);
        return filtered;
    }
}
