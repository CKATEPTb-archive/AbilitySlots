package ru.ckateptb.abilityslots.service;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivateResult;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.enums.SequenceAction;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.ability.sequence.AbilityAction;
import ru.ckateptb.abilityslots.ability.sequence.Sequence;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class AbilitySequenceService implements Listener {
    private final Map<AbilityInformation, List<AbilityAction>> sequences = new HashMap<>();
    private final Map<AbilityUser, List<AbilityAction>> userActions = new HashMap<>();

    private int maxSize = 0;

    private final AbilityInstanceService abilityInstanceService;

    public AbilitySequenceService(AbilityInstanceService abilityInstanceService) {
        this.abilityInstanceService = abilityInstanceService;
    }

    public void registerSequence(AbilityInformation information, Sequence sequence) {
        List<AbilityAction> abilityActions = Arrays.asList(sequence.value());
        maxSize = Math.max(maxSize, abilityActions.size());
        sequences.put(information, abilityActions);
    }

    public ActivateResult registerAction(AbilityUser user, SequenceAction sequenceAction) {
        AbilityInformation information = user.getSelectedAbility();
        if (information == null) return ActivateResult.NOT_ACTIVATE;

        AbilityAction abilityAction = this.createAbilityAction(information, sequenceAction);
        List<AbilityAction> actions = userActions.getOrDefault(user, new ArrayList<>());
        actions.add(abilityAction);
        if (actions.size() > maxSize) actions.remove(0);
        userActions.put(user, actions);


        AtomicReference<ActivateResult> result = new AtomicReference<>(ActivateResult.NOT_ACTIVATE);
        sequences.forEach((key, value) -> {
            if (tailMatches(actions, value) && user.canActivate(key)) {
                Ability instance = key.createAbility();
                ActivateResult activateResult = instance.activate(user, ActivationMethod.SEQUENCE);
                if (activateResult == ActivateResult.ACTIVATE || activateResult == ActivateResult.ACTIVATE_AND_CANCEL_EVENT) {
                    result.set(activateResult);
                    abilityInstanceService.registerInstance(user, instance);
                }
            }
        });
        return result.get();
    }

    public boolean tailMatches(List<AbilityAction> list, List<AbilityAction> tail) {
        if (list.isEmpty() || tail.isEmpty()) return false;

        int listSize = list.size();
        int tailSize = tail.size();
        if (listSize < tailSize) return false;

        for (int i = 0; i < tailSize; ++i) {
            AbilityAction first = list.get(listSize - 1 - i);
            AbilityAction second = tail.get(tailSize - 1 - i);

            if (!equals(first, second)) {
                return false;
            }
        }
        return true;
    }

    private boolean equals(AbilityAction action, AbilityAction other) {
        return action.ability() == other.ability() && action.action() == other.action();
    }

    private AbilityAction createAbilityAction(AbilityInformation information, SequenceAction sequenceAction) {
        return new AbilityAction() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return AbilityAction.class;
            }

            @Override
            public Class<? extends Ability> ability() {
                return information.getAbilityClass();
            }

            @Override
            public SequenceAction action() {
                return sequenceAction;
            }
        };
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(AbilitySlotsReloadEvent event) {
        this.sequences.clear();
        this.userActions.clear();
        this.maxSize = 0;
    }
}
