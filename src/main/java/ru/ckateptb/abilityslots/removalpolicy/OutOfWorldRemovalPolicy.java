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

package ru.ckateptb.abilityslots.removalpolicy;


import org.bukkit.World;
import ru.ckateptb.abilityslots.user.AbilityUser;

public class OutOfWorldRemovalPolicy implements RemovalPolicy {
    private final AbilityUser user;
    private final World world;

    public OutOfWorldRemovalPolicy(AbilityUser user) {
        this.user = user;
        this.world = user.getEntity().getWorld();
    }

    @Override
    public boolean shouldRemove() {
        return !user.getEntity().getWorld().equals(world);
    }
}
