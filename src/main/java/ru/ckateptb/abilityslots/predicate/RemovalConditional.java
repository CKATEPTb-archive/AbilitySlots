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
import org.bukkit.Location;
import org.bukkit.World;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public interface RemovalConditional extends Conditional<Ability> {
    RemovalConditional IS_OFFLINE = (user, ability) -> !user.isOnline();

    RemovalConditional IS_DEAD = (user, ability) -> user.isDead();

    RemovalConditional IS_SNEAKING = (user, ability) -> user.isSneaking();

    RemovalConditional IS_NOT_SNEAKING = (user, ability) -> !user.isSneaking();

    default boolean shouldRemove(AbilityUser user, Ability ability) {
        return this.matches(user, ability);
    }

    @Override
    boolean matches(AbilityUser user, Ability ability);

    @NoArgsConstructor
    class Builder {
        private final Set<RemovalConditional> policies = new HashSet<>();

        public Builder offline() {
            policies.add(IS_OFFLINE);
            return this;
        }

        public Builder dead() {
            policies.add(IS_DEAD);
            return this;
        }

        public Builder world() {
            policies.add((user, ability) -> !Objects.equals(user.getWorld(), ability.getWorld()));
            return this;
        }

        public Builder world(World world) {
            policies.add((user, ability) -> !Objects.equals(user.getWorld(), world));
            return this;
        }

        public Builder sneaking(boolean shouldSneaking) {
            policies.add(shouldSneaking ? IS_NOT_SNEAKING : IS_SNEAKING);
            return this;
        }

        public Builder duration(long duration) {
            if (duration > 0) {
                long expire = System.currentTimeMillis() + duration;
                policies.add((user, ability) -> System.currentTimeMillis() > expire);
            }
            return this;
        }

        public Builder range(Supplier<Location> from, Supplier<Location> to, double range) {
            policies.add((user, ability) -> {
                Location fromLocation = from.get();
                Location toLocation = to.get();
                return !Objects.equals(fromLocation.getWorld(), toLocation.getWorld()) || fromLocation.distance(toLocation) > range;
            });
            return this;
        }

        public Builder canUse(Supplier<Location> location) {
            policies.add((user, ability) -> !user.canUse(location.get()));
            return this;
        }

        public Builder slot() {
            policies.add((user, ability) -> {
                AbilityInformation information = user.getSelectedAbility();
                return user.isPlayer() && (information == null || !information.getAbilityClass().equals(ability.getClass()));
            });
            return this;
        }

        public Builder slot(Class<? extends Ability> type) {
            policies.add((user, ability) -> {
                AbilityInformation information = user.getSelectedAbility();
                return user.isPlayer() && (information == null || !information.getAbilityClass().equals(type));
            });
            return this;
        }

        public Builder costInterval(long interval) {
            AtomicLong expiredEnergySafeTime = new AtomicLong(System.currentTimeMillis() + interval);
            policies.add((user, ability) -> {
                if(System.currentTimeMillis() > expiredEnergySafeTime.get()) {
                    expiredEnergySafeTime.set(System.currentTimeMillis() + interval);
                    return !user.removeEnergy(ability);
                }
                return false;
            });
            return this;
        }

        public Builder costInterval(double amount, long interval) {
            AtomicLong expiredEnergySafeTime = new AtomicLong(System.currentTimeMillis() + interval);
            policies.add((user, ability) -> {
                if(System.currentTimeMillis() > expiredEnergySafeTime.get()) {
                    expiredEnergySafeTime.set(System.currentTimeMillis() + interval);
                    return !user.removeEnergy(amount);
                }
                return false;
            });
            return this;
        }

        public Builder custom(RemovalConditional conditional) {
            policies.add(conditional);
            return this;
        }

        public RemovalConditional build() {
            return (user, ability) -> policies.stream().anyMatch(police -> police.shouldRemove(user, ability));
        }
    }
}
