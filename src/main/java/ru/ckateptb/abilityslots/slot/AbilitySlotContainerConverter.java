package ru.ckateptb.abilityslots.slot;

import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.tablecloth.spring.SpringContext;

import java.io.Serializable;

public class AbilitySlotContainerConverter implements Serializable {
    private final String[] slots = new String[9];

    public AbilitySlotContainerConverter(AbilitySlotContainer container) {
        for (int i = 0; i < 9; i++) {
            AbilityInformation ability = container.getAbility(i + 1);
            slots[i] = ability == null ? null : ability.getName();
        }
    }

    public AbilitySlotContainer convert() {
        AbilityService service = SpringContext.getInstance().getBean(AbilityService.class);
        AbilitySlotContainer container = new DefaultAbilitySlotContainer();
        for (int i = 0; i < 9; i++) {
            container.setAbility(i + 1, service.getAbility(slots[i]));
        }
        return container;
    }
}
