package ru.ckateptb.abilityslots.service;

import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AbilityService {
    private final Map<String, AbilityInformation> abilities = new HashMap<>();
    private final Map<String, AbilityInformation> passives = new HashMap<>();

    public void registerAbility(AbilityInformation ability) {
        String name = ability.getName();
        if(ability.isActivatedBy(ActivationMethod.PASSIVE)) {
            passives.put(name, ability);
        }
        abilities.put(name, ability);
    }

    public AbilityInformation getAbility(String name) {
        return abilities.get(name);
    }

    public Collection<AbilityInformation> getAbilities() {
        return Collections.unmodifiableCollection(abilities.values());
    }

    public Collection<AbilityInformation> getPassiveAbilities() {
        return Collections.unmodifiableCollection(passives.values());
    }
}
