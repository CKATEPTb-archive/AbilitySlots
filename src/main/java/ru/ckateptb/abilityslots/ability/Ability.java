/*
 * Copyright (c) 2022 CKATEPTb <https://github.com/CKATEPTb>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.ckateptb.abilityslots.ability;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import ru.ckateptb.abilityslots.AbilitySlots;
import ru.ckateptb.abilityslots.ability.enums.AbilityCollisionResult;
import ru.ckateptb.abilityslots.ability.enums.ActivateResult;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.enums.UpdateResult;
import ru.ckateptb.abilityslots.ability.info.AbilityInfo;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
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
    private boolean destroyed;

    public final ActivateResult finalActivate(ActivationMethod method) {
        this.destroyed = false;
        return this.activate(method);
    }

    public abstract ActivateResult activate(ActivationMethod method);

    public final UpdateResult finalUpdate() {
        return this.destroyed ? UpdateResult.REMOVE : this.update();
    }

    public abstract UpdateResult update();

    public final void finalDestroy() {
        this.destroyed = true;
        this.destroy();
    }

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

    public void sync(Runnable runnable) {
        if (SpringContext.getInstance().getBean(AbilitySlotsConfig.class).isAsyncAbilities()) {
            Bukkit.getScheduler().runTask(AbilitySlots.getInstance(), runnable);
        } else {
            runnable.run();
        }
    }
}
