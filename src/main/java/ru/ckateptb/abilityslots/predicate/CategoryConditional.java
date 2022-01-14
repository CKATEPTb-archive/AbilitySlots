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

package ru.ckateptb.abilityslots.predicate;

import lombok.NoArgsConstructor;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.HashSet;
import java.util.Set;

public interface CategoryConditional extends Conditional<AbilityCategory> {
    CategoryConditional HAS_CATEGORY_PERMISSION = (user, category) -> category != null && user.hasPermission(String.format("abilityslots.abilities.%s", category.getName()).toLowerCase());

    @NoArgsConstructor
    class Builder {
        private final Set<CategoryConditional> conditionals = new HashSet<>();

        public Builder hasPermission() {
            conditionals.add(HAS_CATEGORY_PERMISSION);
            return this;
        }

        public Builder custom(CategoryConditional conditional) {
            conditionals.add(conditional);
            return this;
        }

        public CategoryConditional build() {
            return (user, ability) -> conditionals.stream().allMatch(conditional -> conditional.matches(user, ability));
        }
    }

    @Override
    boolean matches(AbilityUser user, AbilityCategory ability);
}
