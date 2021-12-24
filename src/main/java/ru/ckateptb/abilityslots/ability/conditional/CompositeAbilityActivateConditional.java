package ru.ckateptb.abilityslots.ability.conditional;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeAbilityActivateConditional implements AbilityActivateConditional {
    private final List<AbilityActivateConditional> conditionals = new ArrayList<>();

    @Override
    public boolean canActivate(AbilityUser user, AbilityInformation ability) {
        return conditionals.stream().allMatch((cond) -> cond.canActivate(user, ability));
    }

    public void add(AbilityActivateConditional... conditionals) {
        this.conditionals.addAll(Arrays.asList(conditionals));
    }

    public void add(Collection<AbilityActivateConditional> conditionals) {
        conditionals.addAll(conditionals);
    }

    public void add(AbilityActivateConditional conditional) {
        conditionals.add(conditional);
    }

    public void remove(AbilityActivateConditional conditional) {
        conditionals.remove(conditional);
    }

    public boolean hasType(Class<? extends AbilityActivateConditional> type) {
        return conditionals.stream().anyMatch(cond -> type.isAssignableFrom(cond.getClass()));
    }

    // Returns all of the conditionals that were removed.
    public List<AbilityActivateConditional> removeType(Class<? extends AbilityActivateConditional> type) {
        // Filter out any conditionals that match the provided type.
        List<AbilityActivateConditional> filtered = conditionals.stream()
                .filter((cond) -> type.isAssignableFrom(cond.getClass()))
                .collect(Collectors.toList());
        conditionals.removeAll(filtered);
        return filtered;
    }
}
