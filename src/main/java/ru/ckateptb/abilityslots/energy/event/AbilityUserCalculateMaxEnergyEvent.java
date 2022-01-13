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

package ru.ckateptb.abilityslots.energy.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import ru.ckateptb.abilityslots.user.AbilityUser;

@AllArgsConstructor
@Getter
@Setter
public class AbilityUserCalculateMaxEnergyEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private final AbilityUser user;
    private double maxEnergy;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
