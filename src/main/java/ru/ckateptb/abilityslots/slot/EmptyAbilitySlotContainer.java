package ru.ckateptb.abilityslots.slot;

import org.springframework.stereotype.Component;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;

public class EmptyAbilitySlotContainer implements AbilitySlotContainer {
    @Override
    public AbilityInformation[] getAbilities() {
        return new AbilityInformation[9];
    }

    @Override
    public AbilityInformation getAbility(int slot) {
        return null;
    }

    @Override
    public void setAbility(int slot, AbilityInformation ability) {

    }
}
