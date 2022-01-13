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

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import ru.ckateptb.abilityslots.ability.conditional.*;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.board.AbilityBoardHolder;
import ru.ckateptb.abilityslots.slot.AbilitySlotContainer;
import ru.ckateptb.abilityslots.slot.DefaultAbilitySlotContainer;

import java.util.HashMap;
import java.util.Map;

public class LivingEntityAbilityUser implements AbilityUser, AbilityBoardHolder {
    protected final LivingEntity livingEntity;
    private final Map<AbilityInformation, Long> cooldowns = new HashMap<>();
    protected AbilitySlotContainer slotContainer;
    private CompositeAbilityConditional abilityActivateConditional = new CompositeAbilityConditional();

    public LivingEntityAbilityUser(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.slotContainer = new DefaultAbilitySlotContainer();
        this.abilityActivateConditional.add(
                new CategoryAbilityConditional(),
                new CooldownAbilityConditional(),
                new EnergyAbilityConditional(),
                new EnabledAbilityConditional(),
                new GameModeAbilityConditional(GameMode.SPECTATOR),
                new PermissionAbilityConditional()
        );
    }

    @Override
    public LivingEntity getEntity() {
        return livingEntity;
    }

    @Override
    public AbilitySlotContainer getSlotContainer() {
        return slotContainer;
    }

    @Override
    public void setSlotContainer(AbilitySlotContainer slotContainer) {
        this.slotContainer = slotContainer;
    }

    @Override
    public AbilityInformation getSelectedAbility() {
        return null;
    }

    @Override
    public void clearAbilities() {
        for (int i = 1; i <= 9; i++) {
            setAbility(i, null);
        }
    }

    @Override
    public void setCooldown(AbilityInformation ability, long duration) {
        long current = cooldowns.getOrDefault(ability, 0L);

        // Only set cooldown if the new one is larger.
        if (duration > 0 && duration > current - System.currentTimeMillis()) {
            cooldowns.put(ability, System.currentTimeMillis() + duration);
        }
    }

    @Override
    public boolean hasCooldown(AbilityInformation ability) {
        if (!cooldowns.containsKey(ability)) {
            return false;
        }

        long time = System.currentTimeMillis();
        long end = cooldowns.get(ability);

        return time < end;
    }

    @Override
    public Map<AbilityInformation, Long> getCooldowns() {
        return cooldowns;
    }

    @Override
    public boolean canActivate(AbilityInformation ability) {
        return abilityActivateConditional.matches(this, ability);
    }

    @Override
    public CompositeAbilityConditional getAbilityActivateConditional() {
        return abilityActivateConditional;
    }

    @Override
    public void setAbilityActivateConditional(CompositeAbilityConditional conditional) {
        this.abilityActivateConditional = conditional;
    }

    @Override
    public boolean canUse(Location location) {
        return true;
    }

    @Override
    public void updateAbilityBoard() {

    }

    @Override
    public void enableAbilityBoard() {

    }

    @Override
    public void disableAbilityBoard() {

    }

    @Override
    public boolean isAbilityBoardEnabled() {
        return false;
    }

    @Override
    public double getEnergy() {
        return Double.MAX_VALUE;
    }

    @Override
    public boolean removeEnergy(double value) {
        return true;
    }

    @Override
    public void addEnergy(double value) {
    }

    @Override
    public void setEnergy(double value) {
    }

    @Override
    public double getMaxEnergy() {
        return Double.MAX_VALUE;
    }

    @Override
    public void updateEnergyBar() {

    }

    @Override
    public void enableEnergyBar() {

    }

    @Override
    public void disableEnergyBar() {

    }

    @Override
    public boolean isEnergyBarEnabled() {
        return false;
    }
}
