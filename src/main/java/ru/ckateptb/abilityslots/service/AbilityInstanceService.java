package ru.ckateptb.abilityslots.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivateResult;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.enums.UpdateResult;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
// TODO Это не мой код, его нужно переписать
public class AbilityInstanceService implements Listener {
    private final Map<AbilityUser, List<Ability>> instances = new HashMap<>();
    private final AbilityService abilityService;

    public AbilityInstanceService(AbilityService abilityService) {
        this.abilityService = abilityService;
    }

    public void registerInstance(AbilityUser user, Ability instance) {
        instances.computeIfAbsent(user, (key) -> new ArrayList<>()).add(instance);
    }

    @Scheduled(fixedRate = 1)
    public void update() {
        Iterator<Map.Entry<AbilityUser, List<Ability>>> playerIterator = instances.entrySet().iterator();

        List<Ability> removed = new ArrayList<>();

        while (playerIterator.hasNext()) {
            Map.Entry<AbilityUser, List<Ability>> entry = playerIterator.next();
            List<Ability> instances = entry.getValue();
            Iterator<Ability> iterator = instances.iterator();
            while (iterator.hasNext()) {
                Ability ability = iterator.next();
                UpdateResult result = UpdateResult.REMOVE;
                try {
                    result = ability.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result == UpdateResult.REMOVE) {
                    removed.add(ability);
                    iterator.remove();
                }
            }
            if (entry.getValue().isEmpty()) {
                playerIterator.remove();
            }
        }

        removed.forEach(this::destroyInstance);
    }

    private void destroyInstance(Ability ability) {
        ability.destroy();
    }

    public void changeOwner(Ability ability, AbilityUser user) {
        if (ability.getUser().equals(user)) return;

        List<Ability> previousUserInstances = instances.get(ability.getUser());
        if (previousUserInstances != null) {
            previousUserInstances.remove(ability);
        }
        instances.computeIfAbsent(user, k -> new ArrayList<>()).add(ability);
        ability.setUser(user);
    }

    public void createPassives(AbilityUser user) {
        Collection<AbilityInformation> passives = abilityService.getPassiveAbilities();
        for (AbilityInformation passive : passives) {
            destroyInstanceType(user, passive);
            if (!passive.isEnabled()) continue;
            if (!user.canActivate(passive)) continue;
            Ability ability = passive.createAbility();
            ActivateResult activateResult = ability.activate(user, ActivationMethod.PASSIVE);
            if (activateResult == ActivateResult.ACTIVATE || activateResult == ActivateResult.ACTIVATE_AND_CANCEL_EVENT) {
                this.registerInstance(user, ability);
            }
        }
    }

    public void clearPassives(AbilityUser user) {
        List<Ability> abilities = new ArrayList<>(getAbilityUserInstances(user));
        for (Ability instance : abilities) {
            if (instance.getInformation().isActivatedBy(ActivationMethod.PASSIVE)) {
                this.destroyInstance(user, instance);
            }
        }
    }

    public boolean hasAbility(AbilityUser user, Class<? extends Ability> abilityType) {
        List<Ability> abilities = instances.get(user);
        if (abilities == null) return false;
        for (Ability ability : abilities) {
            if (ability.getClass().equals(abilityType)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAbility(AbilityUser user, AbilityInformation information) {
        return hasAbility(user, information.getAbilityClass());
    }

    public void destroyInstance(AbilityUser user, Ability ability) {
        List<Ability> abilities = instances.get(user);
        if (ability == null) {
            return;
        }
        abilities.remove(ability);
        destroyInstance(ability);
    }

    public boolean destroyInstanceType(AbilityUser user, AbilityInformation information) {
        if (information == null) return false;
        return destroyInstanceType(user, information.getAbilityClass());
    }

    public boolean destroyInstanceType(AbilityUser user, Class<? extends Ability> clazz) {
        List<Ability> abilities = instances.get(user);
        if (abilities == null) return false;
        boolean destroyed = false;
        for (Iterator<Ability> iterator = abilities.iterator(); iterator.hasNext(); ) {
            Ability ability = iterator.next();
            if (ability.getClass() == clazz) {
                iterator.remove();
                destroyInstance(ability);
                destroyed = true;
            }
        }
        return destroyed;
    }

    // Get the number of active abilities.
    public int getInstanceCount() {
        int size = 0;
        for (List<Ability> instances : instances.values()) {
            size += instances.size();
        }
        return size;
    }

    public List<Ability> getAbilityUserInstances(AbilityUser user) {
        List<Ability> abilities = instances.get(user);
        if (abilities == null) return new ArrayList<>();
        return abilities;
    }

    @SuppressWarnings("unchecked")
    public <T extends Ability> List<T> getAbilityUserInstances(AbilityUser user, Class<T> type) {
        List<Ability> abilities = instances.get(user);
        if (abilities == null) return new ArrayList<>();
        return abilities.stream().filter(a -> a.getClass() == type).map(a -> (T) a).collect(Collectors.toList());
    }

    public List<Ability> getInstances() {
        List<Ability> totalInstances = new ArrayList<>();
        for (List<Ability> instances : instances.values()) {
            totalInstances.addAll(instances);
        }
        return totalInstances;
    }

    @SuppressWarnings("unchecked")
    public <T extends Ability> List<T> getInstances(Class<T> type) {
        List<T> totalInstances = new ArrayList<>();
        for (List<Ability> instances : instances.values()) {
            for (Ability ability : instances) {
                if (ability.getClass().equals(type)) {
                    totalInstances.add((T) ability);
                }
            }
        }
        return totalInstances;
    }

    public void destroyAbilityUserInstances(AbilityUser user) {
        List<Ability> instances = this.instances.get(user);
        if (instances != null) {
            for (Ability ability : instances) {
                destroyInstance(ability);
            }
            instances.clear();
        }
        this.instances.remove(user);
    }

    public void destroyAllInstances() {
        Iterator<Map.Entry<AbilityUser, List<Ability>>> playerIterator = instances.entrySet().iterator();
        while (playerIterator.hasNext()) {
            Map.Entry<AbilityUser, List<Ability>> entry = playerIterator.next();
            List<Ability> instances = entry.getValue();
            for (Ability ability : instances) {
                destroyInstance(ability);
            }
            instances.clear();
            playerIterator.remove();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(AbilitySlotsReloadEvent event) {
        this.destroyAllInstances();
    }

    @EventListener
    public void handleContextRefreshEvent(ContextClosedEvent ignored) {
        this.destroyAllInstances();
    }
}
