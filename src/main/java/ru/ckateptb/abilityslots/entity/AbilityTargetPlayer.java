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

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.MainHand;
import org.jetbrains.annotations.NotNull;

public interface AbilityTargetPlayer extends AbilityTargetLiving {

    /**
     * @return wrapped {@link Player}
     */
    Player getEntity();

    /**
     * @return true if player is in sneak mode,
     */
    @Override
    default boolean isSneaking() {
        return getEntity().isSneaking();
    }

    /**
     * Gets whether the player is sprinting or not.
     *
     * @return true if player is sprinting.
     */
    @Override
    default boolean isSprinting() {
        return getEntity().isSprinting();
    }

    /**
     * Gets the value of the specified permission, if set.
     * <p>
     * If a permission override is not set on this object, the default value
     * of the permission will be returned.
     *
     * @param permission Name of the permission
     * @return Value of the permission
     */
    @Override
    default boolean hasPermission(String permission) {
        return getEntity().hasPermission(permission);
    }

    /**
     * Gets this human's current {@link GameMode}
     *
     * @return Current game mode
     */
    default GameMode getGameMode() {
        return getEntity().getGameMode();
    }

    /**
     * Checks if this player is currently online
     *
     * @return true if they are online
     */
    @Override
    default boolean isOnline() {
        return getEntity().isOnline();
    }

    /**
     * @return true.
     */
    default boolean isLiving() {
        return true;
    }

    /**
     * @return true.
     */
    default boolean isPlayer() {
        return true;
    }

    /**
     * Gets the player's selected main hand
     *
     * @return the players main hand
     */
    default MainHand getMainHand() {
        return getEntity().getMainHand();
    }
}
