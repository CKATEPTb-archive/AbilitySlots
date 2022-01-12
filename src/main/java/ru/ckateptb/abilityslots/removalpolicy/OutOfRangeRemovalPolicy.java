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

import org.bukkit.Location;

import java.util.Objects;
import java.util.function.Supplier;

public class OutOfRangeRemovalPolicy implements RemovalPolicy {
    private final Supplier<Location> fromSupplier;
    private final Supplier<Location> toSupplier;
    private final double range;

    public OutOfRangeRemovalPolicy(Supplier<Location> fromSupplier, Supplier<Location> toSupplier, double range) {
        this.fromSupplier = fromSupplier;
        this.toSupplier = toSupplier;
        this.range = range;
    }

    @Override
    public boolean shouldRemove() {
        if (this.range == 0) return false;

        Location from = this.fromSupplier.get();
        Location to = this.toSupplier.get();

        if (!Objects.equals(from.getWorld(), to.getWorld())) return true;
        return from.distance(to) >= this.range;
    }
}
