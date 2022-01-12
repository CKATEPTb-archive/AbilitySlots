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

package ru.ckateptb.abilityslots.user;


import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import ru.ckateptb.tablecloth.math.ImmutableVector;

public interface AbilityTargetEntity {
    Entity getEntity();

    default void setVelocity(Vector velocity) {
        getEntity().setVelocity(velocity);
    }

    default ImmutableVector getLocation() {
        return new ImmutableVector(getEntity().getLocation());
    }

    default ImmutableVector getCenterLocation() {
        return getLocation().add(new ImmutableVector(0, getEntity().getHeight() / 2, 0));
    }

    default double getDistanceAboveGround() {
        return getDistanceAboveGround(false);
    }

    default double getDistanceAboveGround(boolean ignoreLiquids) {
        return getLocation().getDistanceAboveGround(getWorld(), ignoreLiquids);
    }

    default World getWorld() {
        return getEntity().getWorld();
    }
}
