package ru.ckateptb.abilityslots.ability.conditional;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeAbilityConditional implements AbilityConditional {
    private final List<AbilityConditional> conditionals = new ArrayList<>();

    @Override
    public boolean matches(AbilityUser user, AbilityInformation ability) {
        return conditionals.stream().allMatch((cond) -> cond.matches(user, ability));
    }

    public void add(AbilityConditional... conditionals) {
        this.conditionals.addAll(Arrays.asList(conditionals));
    }

    public void add(Collection<AbilityConditional> conditionals) {
        this.conditionals.addAll(conditionals);
    }

    public void add(AbilityConditional conditional) {
        conditionals.add(conditional);
    }

    public void remove(AbilityConditional conditional) {
        conditionals.remove(conditional);
    }

    public boolean hasType(Class<? extends AbilityConditional> type) {
        return conditionals.stream().anyMatch(cond -> type.isAssignableFrom(cond.getClass()));
    }

    // Returns all of the conditionals that were removed.
    public List<AbilityConditional> removeType(Class<? extends AbilityConditional> type) {
        // Filter out any conditionals that match the provided type.
        List<AbilityConditional> filtered = conditionals.stream()
                .filter((cond) -> type.isAssignableFrom(cond.getClass()))
                .collect(Collectors.toList());
        conditionals.removeAll(filtered);
        return filtered;
    }
}
