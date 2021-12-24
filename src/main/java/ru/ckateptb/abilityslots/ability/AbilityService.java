package ru.ckateptb.abilityslots.ability;

import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AbilityService {
    private final Map<String, AbilityInformation> abilities = new HashMap<>();

    public void registerAbility(AbilityInformation ability) {
        abilities.put(ability.getName(), ability);
    }

    public AbilityInformation getAbility(String name) {
        return abilities.get(name);
    }

    public Collection<AbilityInformation> getAbilities() {
        return Collections.unmodifiableCollection(abilities.values());
    }
}
