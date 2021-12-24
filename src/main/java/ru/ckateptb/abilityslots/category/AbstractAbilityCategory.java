package ru.ckateptb.abilityslots.category;

import lombok.Getter;
import lombok.Setter;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public abstract class AbstractAbilityCategory implements AbilityCategory {
    private final List<AbilityInformation> abilities = new ArrayList<>();
    private boolean enabled = true;

    @Override
    public void registerAbility(AbilityInformation ability) {
        abilities.add(ability);
    }
}
