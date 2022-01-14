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
import org.bukkit.GameMode;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface AbilityConditional extends Conditional<AbilityInformation> {
    AbilityConditional IS_BINDABLE = (user, ability) -> ability != null && ability.isCanBindToSlot();

    AbilityConditional HAS_ABILITY_PERMISSION = (user, ability) -> ability != null && user.hasPermission(String.format("abilityslots.abilities.%s.%s", ability.getCategory().getName(), ability.getName()).toLowerCase());

    AbilityConditional HAS_ABILITY_CATEGORY_PERMISSION = (user, ability) -> ability != null && CategoryConditional.HAS_CATEGORY_PERMISSION.matches(user, ability.getCategory());

    AbilityConditional NOT_ON_COOLDOWN = (user, ability) -> ability != null && !user.hasCooldown(ability);

    AbilityConditional IS_ENABLED = (user, ability) -> ability != null && ability.isEnabled();

    AbilityConditional ENOUGH_ENERGY = (user, ability) -> ability != null && ability.getCost() <= user.getEnergy();

    @Override
    boolean matches(AbilityUser user, AbilityInformation ability);

    @NoArgsConstructor
    class Builder {
        private final Set<AbilityConditional> conditionals = new HashSet<>();

        public Builder isEnabled() {
            conditionals.add(IS_ENABLED);
            return this;
        }

        public Builder isBindable() {
            conditionals.add(IS_BINDABLE);
            return this;
        }

        public Builder hasPermission() {
            conditionals.add(HAS_ABILITY_PERMISSION);
            return this;
        }

        public Builder hasCategory() {
            conditionals.add(HAS_ABILITY_CATEGORY_PERMISSION);
            return this;
        }

        public Builder withoutCooldown() {
            conditionals.add(NOT_ON_COOLDOWN);
            return this;
        }

        public Builder enoughEnergy() {
            conditionals.add(ENOUGH_ENERGY);
            return this;
        }

        public Builder gameModeNot(GameMode... gameModes) {
            conditionals.add((user, ability) -> !user.isPlayer() || !Arrays.asList(gameModes).contains(user.getGameMode()));
            return this;
        }

        public Builder custom(AbilityConditional conditional) {
            conditionals.add(conditional);
            return this;
        }

        public AbilityConditional build() {
            return (user, ability) -> conditionals.stream().allMatch(conditional -> conditional.matches(user, ability));
        }
    }
}
