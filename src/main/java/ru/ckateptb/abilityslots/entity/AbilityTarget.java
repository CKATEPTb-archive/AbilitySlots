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

package ru.ckateptb.abilityslots.entity;


import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.tablecloth.math.ImmutableVector;
import ru.ckateptb.tablecloth.util.WorldUtils;

/**
 * Entity wrapper
 */
public interface AbilityTarget {
    /**
     * Wraps the specified {@link Entity} in {@link AbilityTarget}.
     *
     * @param entity {@link Entity} that should to wrap
     * @return {@link AbilityTarget} of specified {@link Entity}
     */
    static AbilityTarget of(Entity entity) {
        return () -> entity;
    }

    /**
     * Wraps the specified {@link LivingEntity} in {@link AbilityTargetLiving}.
     *
     * @param entity {@link LivingEntity} that should to wrap
     * @return {@link AbilityTargetLiving} of specified {@link LivingEntity}
     */
    static AbilityTargetLiving of(LivingEntity entity) {
        return () -> entity;
    }

    /**
     * Wraps the specified {@link Player} in {@link AbilityTargetPlayer}.
     *
     * @param entity {@link Player} that should to wrap
     * @return {@link AbilityTargetPlayer} of specified {@link Player}
     */
    static AbilityTargetPlayer of(Player entity) {
        return () -> entity;
    }

    /**
     * @return wrapped {@link Entity}
     */
    Entity getEntity();

    /**
     * Apply {@link Entity#setVelocity(Vector)} for wrapped {@link Entity}.
     * @param velocity – New velocity to travel with
     * @param ability {@link Ability} that applied Velocity
     */
    default void setVelocity(Vector velocity, Ability ability) {
        getEntity().setVelocity(velocity);
    }

    /**
     * Gets the ImmutableVector of wrapped entity current position.
     * @return the resulting {@link ImmutableVector}
     */
    default ImmutableVector getLocation() {
        return new ImmutableVector(getEntity().getLocation());
    }

    /**
     * Calculates a vector at the center of the wrapped entity using its height.
     * @return the resulting {@link ImmutableVector}
     */
    default ImmutableVector getCenterLocation() {
        return getLocation().add(new ImmutableVector(0, getEntity().getHeight() / 2, 0));
    }

    /**
     * Calculates the distance between an entity and the ground using precise {@link ru.ckateptb.tablecloth.collision.collider.RayCollider} colliders.
     * @return the distance in blocks between the wrapped entity and ground or void
     */
    default double getDistanceAboveGround() {
        return getDistanceAboveGround(false);
    }

    /**
     * Calculates the distance between an entity and the ground using precise {@link ru.ckateptb.tablecloth.collision.collider.RayCollider} colliders.
     * @param ignoreLiquids if false - consider liquid as ground when calculating
     * @return the distance in blocks between the wrapped entity and ground or void
     */
    default double getDistanceAboveGround(boolean ignoreLiquids) {
        return getLocation().getDistanceAboveGround(getWorld(), ignoreLiquids);
    }

    /**
     * Gets the current world of wrapped entity resides in
     * @return World
     */
    default World getWorld() {
        return getEntity().getWorld();
    }

    /**
     * Returns true if the entity is supported by a block. This value is a
     * state updated by the server and is not recalculated unless the entity
     * moves.
     *
     * @return True if entity is on ground.
     */
    default boolean isOnGround() {
        return WorldUtils.isOnGround(getEntity());
    }

    /**
     * @return false.
     */
    default boolean isLiving() {
        return false;
    }

    /**
     * @return false.
     */
    default boolean isPlayer() {
        return false;
    }
}
