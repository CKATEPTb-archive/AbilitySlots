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

package ru.ckateptb.abilityslots.category;

import lombok.Getter;
import lombok.Setter;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public abstract class AbstractAbilityCategory implements AbilityCategory {
    private final Map<String, AbilityInformation> abilities = new HashMap<>();
    private boolean enabled = true;

    @Override
    public void registerAbility(AbilityInformation ability) {
        abilities.put(ability.getName(), ability);
    }

    @Override
    public AbilityInformation getAbility(String name) {
        return abilities.get(name);
    }

    public Collection<AbilityInformation> getAbilities() {
        return Collections.unmodifiableCollection(abilities.values());
    }
}
