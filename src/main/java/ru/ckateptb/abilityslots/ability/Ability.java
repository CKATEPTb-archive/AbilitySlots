package ru.ckateptb.abilityslots.ability;

import lombok.Getter;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import ru.ckateptb.abilityslots.ability.enums.AbilityCollisionResult;
import ru.ckateptb.abilityslots.ability.enums.ActivateResult;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.enums.UpdateResult;
import ru.ckateptb.abilityslots.ability.info.AbilityInfo;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.service.AbilityInstanceService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.tablecloth.collision.Collider;
import ru.ckateptb.tablecloth.spring.SpringContext;

import java.util.Collection;
import java.util.Collections;

@Getter
public abstract class Ability {
    protected AbilityUser user;
    protected LivingEntity livingEntity;
    protected World world;

    public abstract ActivateResult activate(ActivationMethod method);

    public abstract UpdateResult update();

    public abstract void destroy();

    public void setUser(AbilityUser user) {
        this.user = user;
        this.livingEntity = user.getEntity();
        this.world = livingEntity.getWorld();
    }

    public AbilityInformation getInformation() {
        AbilityService abilityService = SpringContext.getInstance().getBean(AbilityService.class);
        AbilityInfo info = getClass().getAnnotation(AbilityInfo.class);
        return abilityService.getAbility(info.name());
    }

    public Collection<Collider> getColliders() {
        return Collections.emptySet();
    }

    public AbilityCollisionResult destroyCollider(Ability destroyer, Collider destroyerCollider, Collider destroyedCollider) {
        return AbilityCollisionResult.DESTROY_INSTANCE;
    }

    public AbilityInstanceService getAbilityInstanceService() {
        return SpringContext.getInstance().getBean(AbilityInstanceService.class);
    }
}
