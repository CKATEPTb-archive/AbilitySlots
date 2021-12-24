package ru.ckateptb.abilityslots.slot;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;

public class DefaultAbilitySlotContainer implements AbilitySlotContainer {
    private final AbilityInformation[] abilities = new AbilityInformation[9];

    @Override
    public AbilityInformation[] getAbilities() {
        return abilities;
    }

    @Override
    public AbilityInformation getAbility(int slot) {
        return abilities[slot - 1];
    }

    @Override
    public void setAbility(int slot, AbilityInformation ability) {
        abilities[slot - 1] = ability;
    }
}
