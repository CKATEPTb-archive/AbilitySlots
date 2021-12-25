package ru.ckateptb.abilityslots.ability;

import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.enums.UpdateResult;
import ru.ckateptb.abilityslots.ability.info.AbilityInfo;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.service.AbilityInstanceService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.tablecloth.spring.SpringContext;

public interface Ability {
    boolean activate(AbilityUser user, ActivationMethod method);

    UpdateResult update();

    void destroy();

    AbilityUser getUser();

    void setUser(AbilityUser user);

    default AbilityInformation getInformation() {
        AbilityService abilityService = SpringContext.getInstance().getBean(AbilityService.class);
        AbilityInfo info = getClass().getAnnotation(AbilityInfo.class);
        return abilityService.getAbility(info.name());
    }

    default AbilityInstanceService getAbilityInstanceService() {
        return SpringContext.getInstance().getBean(AbilityInstanceService.class);
    }
}
