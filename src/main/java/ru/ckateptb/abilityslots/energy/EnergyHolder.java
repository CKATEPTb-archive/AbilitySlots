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

package ru.ckateptb.abilityslots.energy;

public interface EnergyHolder {
    double getEnergy();

    boolean removeEnergy(double value);

    void addEnergy(double value);

    void setEnergy(double value);

    double getMaxEnergy();

    void updateEnergyBar();

    void enableEnergyBar();

    void disableEnergyBar();

    boolean isEnergyBarEnabled();
}
