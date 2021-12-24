package ru.ckateptb.abilityslots.slot;

import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;

public interface AbilitySlotContainer {
    AbilityInformation[] getAbilities();
    AbilityInformation getAbility(int slot);
    void setAbility(int slot, AbilityInformation desc);
}
