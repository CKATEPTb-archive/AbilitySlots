package ru.ckateptb.abilityslots.user;

import org.bukkit.entity.LivingEntity;
import ru.ckateptb.abilityslots.ability.conditional.CompositeAbilityActivateConditional;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.slot.AbilitySlotContainer;

import java.util.Map;

public interface AbilityUser {
    LivingEntity getEntity();

    AbilitySlotContainer getSlotContainer();

    void setSlotContainer(AbilitySlotContainer slotContainer);

    default AbilityInformation[] getAbilities() {
        return getSlotContainer().getAbilities();
    }

    default AbilityInformation getAbility(int slot) {
        return getSlotContainer().getAbility(slot);
    }

    void clearAbilities();

    default void setAbility(int slot, AbilityInformation ability) {
        getSlotContainer().setAbility(slot, ability);
    }

    void setCooldown(AbilityInformation ability, long duration);

    boolean hasCooldown(AbilityInformation ability);

    Map<AbilityInformation, Long> getCooldowns();

    boolean canActivate(AbilityInformation ability);

    CompositeAbilityActivateConditional getAbilityActivateConditional();

    void setAbilityActivateConditional(CompositeAbilityActivateConditional conditional);
}
