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

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.predicate.AbilityConditional;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.energy.EnergyHolder;
import ru.ckateptb.abilityslots.entity.AbilityTargetLiving;
import ru.ckateptb.abilityslots.service.AbilityInstanceService;
import ru.ckateptb.abilityslots.slot.AbilitySlotContainer;
import ru.ckateptb.tablecloth.spring.SpringContext;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public interface AbilityUser extends AbilityTargetLiving, EnergyHolder {

    @Override
    LivingEntity getEntity();

    AbilitySlotContainer getSlotContainer();

    void setSlotContainer(AbilitySlotContainer slotContainer);

    default AbilityInformation[] getAbilities() {
        return getSlotContainer().getAbilities();
    }

    default AbilityInformation getAbility(int slot) {
        return getSlotContainer().getAbility(slot);
    }

    AbilityInformation getSelectedAbility();

    void clearAbilities();

    default void setAbility(int slot, AbilityInformation ability) {
        getSlotContainer().setAbility(slot, ability);
    }

    void setCooldown(AbilityInformation ability, long duration);

    boolean hasCooldown(AbilityInformation ability);

    Map<AbilityInformation, Long> getCooldowns();

    boolean canActivate(AbilityInformation ability);

    AbilityConditional getAbilityActivateConditional();

    boolean addAbilityActivateConditional(AbilityConditional conditional);

    boolean removeAbilityActivateConditional(AbilityConditional conditional);

    void setAbilityActivateConditional(AbilityConditional conditional);

    boolean canUse(Location location);

    default Collection<? extends Ability> getActiveAbilities() {
        return SpringContext.getInstance().getBean(AbilityInstanceService.class).getAbilityUserInstances(this);
    }

    default Collection<? extends Ability> getActiveAbilities(Class<? extends Ability> type) {
        return SpringContext.getInstance().getBean(AbilityInstanceService.class).getAbilityUserInstances(this, type);
    }

    default Collection<? extends Ability> getPassives() {
        return getActiveAbilities().stream().filter(ability -> ability.getInformation().isActivatedBy(ActivationMethod.PASSIVE)).collect(Collectors.toList());
    }

    default boolean destroyAbility(Class<? extends Ability> type) {
        return SpringContext.getInstance().getBean(AbilityInstanceService.class).destroyInstanceType(this, type);
    }

    default void destroyAbility(Ability ability) {
        SpringContext.getInstance().getBean(AbilityInstanceService.class).destroyInstance(this, ability);
    }

    default void destroyAbilities() {
        SpringContext.getInstance().getBean(AbilityInstanceService.class).destroyAbilityUserInstances(this);
    }

    default void setCooldown(Ability ability) {
        setCooldown(ability.getInformation());
    }

    default void setCooldown(AbilityInformation information) {
        setCooldown(information, information.getCooldown());
    }

    default boolean removeEnergy(Ability ability) {
        return removeEnergy(ability.getInformation());
    }

    default boolean removeEnergy(AbilityInformation information) {
        return removeEnergy(information.getCost());
    }
}
