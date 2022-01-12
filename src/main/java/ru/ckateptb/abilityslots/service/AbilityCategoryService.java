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

import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AbilityCategoryService implements Listener {
    private final Map<String, AbilityCategory> categories = new HashMap<>();

    public void registerCategory(AbilityCategory abilityCategory) {
        categories.putIfAbsent(abilityCategory.getName().toLowerCase(), abilityCategory);
    }

    public Collection<AbilityCategory> getCategories() {
        return categories.values();
    }

    public AbilityCategory getCategory(String name) {
        if (name == null) return null;
        return categories.get(name.toLowerCase());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(AbilitySlotsReloadEvent event) {
        this.categories.clear();
    }
}
