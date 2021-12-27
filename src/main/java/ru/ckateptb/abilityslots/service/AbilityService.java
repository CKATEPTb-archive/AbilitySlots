package ru.ckateptb.abilityslots.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AbilityService implements Listener {
    private final Map<String, AbilityInformation> abilities = new HashMap<>();
    private final Map<String, AbilityInformation> passives = new HashMap<>();

    public void registerAbility(AbilityInformation ability) {
        String name = ability.getName().toLowerCase();
        if (ability.isActivatedBy(ActivationMethod.PASSIVE)) {
            passives.put(name, ability);
        }
        abilities.put(name, ability);
    }

    public AbilityInformation getAbility(String name) {
        if(name == null) return null;
        return abilities.get(name.toLowerCase());
    }

    public Collection<AbilityInformation> getAbilities() {
        return Collections.unmodifiableCollection(abilities.values());
    }

    public Collection<AbilityInformation> getPassiveAbilities() {
        return Collections.unmodifiableCollection(passives.values());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void on(AbilitySlotsReloadEvent event) {
        this.abilities.clear();
        this.passives.clear();
    }
}
