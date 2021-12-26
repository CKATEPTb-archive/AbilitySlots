package ru.ckateptb.abilityslots.slot;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

public interface AbilitySlotContainer {
    AbilityInformation[] getAbilities();

    AbilityInformation getAbility(int slot);

    void setAbility(int slot, AbilityInformation ability);

    void validate(AbilityUser user);
}
