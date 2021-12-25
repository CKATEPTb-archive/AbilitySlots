package ru.ckateptb.abilityslots.category;

import lombok.Getter;
import lombok.Setter;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public abstract class AbstractAbilityCategory implements AbilityCategory {
    private final Map<String, AbilityInformation> abilities = new HashMap<>();
    private boolean enabled = true;

    @Override
    public void registerAbility(AbilityInformation ability) {
        abilities.put(ability.getName(), ability);
    }

    @Override
    public AbilityInformation getAbility(String name) {
        return abilities.get(name);
    }

    public Collection<AbilityInformation> getAbilities() {
        return Collections.unmodifiableCollection(abilities.values());
    }
}
