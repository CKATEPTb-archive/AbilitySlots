package ru.ckateptb.abilityslots.slot;

import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;
import ru.ckateptb.tablecloth.spring.SpringContext;

import java.util.Arrays;
import java.util.stream.Collectors;

public class DefaultAbilitySlotContainer implements AbilitySlotContainer {
    private final AbilityInformation[] abilities = new AbilityInformation[9];

    @Override
    public AbilityInformation[] getAbilities() {
        return abilities;
    }

    @Override
    public AbilityInformation getAbility(int slot) {
        return abilities[slot - 1];
    }

    @Override
    public void setAbility(int slot, AbilityInformation ability) {
        abilities[slot - 1] = ability;
    }

    @Override
    public void validate(AbilityUser user) {
        if (user instanceof PlayerAbilityUser playerAbilityUser) {
            for (int i = 1; i <= 9; i++) {
                AbilityInformation info = getAbility(i);
                if (info == null) continue;
                if (!playerAbilityUser.canBind(info)) {
                    setAbility(i, null);
                }
            }
        }
    }

    @Override
    public String toString() {
        return Arrays.stream(abilities).map(info -> {
            if (info == null) return " ";
            return info.getName();
        }).collect(Collectors.joining("|"));
    }

    public static DefaultAbilitySlotContainer fromString(String string) {
        DefaultAbilitySlotContainer container = new DefaultAbilitySlotContainer();
        String[] names = string.split("\\|");
        AbilityService service = SpringContext.getInstance().getBean(AbilityService.class);
        for (int i = 1; i <= 9; i++) {
            if (names.length < i) break;
            AbilityInformation info = service.getAbility(names[i - 1]);
            if(info == null) continue;
            if (info.isEnabled() && info.isCanBindToSlot()) {
                container.setAbility(i, info);
            }
        }
        return container;
    }
}
