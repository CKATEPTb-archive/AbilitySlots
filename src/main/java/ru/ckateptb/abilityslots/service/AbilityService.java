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

package ru.ckateptb.abilityslots.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AbilityService implements Listener {
    private final Map<String, AbilityInformation> abilities = new HashMap<>();
    private final Map<String, AbilityInformation> passives = new HashMap<>();

    public void registerAbility(AbilityInformation ability) {
        String name = ability.getName().toLowerCase();
        if (ability.isActivatedBy(ActivationMethod.PASSIVE)) {
            passives.put(name, ability);
        }
        abilities.put(name, ability);
    }

    public AbilityInformation getAbility(String name) {
        if (name == null) return null;
        return abilities.get(name.toLowerCase());
    }

    public Collection<AbilityInformation> getAbilities() {
        return Collections.unmodifiableCollection(abilities.values());
    }

    public Collection<AbilityInformation> getPassiveAbilities() {
        return Collections.unmodifiableCollection(passives.values());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(AbilitySlotsReloadEvent event) {
        this.abilities.clear();
        this.passives.clear();
    }
}
