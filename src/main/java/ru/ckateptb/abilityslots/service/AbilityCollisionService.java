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

package ru.ckateptb.abilityslots.service;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.AbilityCollisionResult;
import ru.ckateptb.abilityslots.ability.info.CollisionParticipant;
import ru.ckateptb.tablecloth.collision.Collider;

import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class AbilityCollisionService {
    private final AbilityInstanceService abilityInstanceService;

    public AbilityCollisionService(AbilityInstanceService abilityInstanceService) {
        this.abilityInstanceService = abilityInstanceService;
    }

    @Scheduled(fixedRate = 1)
    public void update() {
        Collection<Ability> instances = abilityInstanceService.getInstances().stream()
                .filter(ability -> AnnotatedElementUtils.isAnnotated(ability.getClass(), CollisionParticipant.class))
                .filter(ability -> !ability.getColliders().isEmpty())
                .collect(Collectors.toList());
        List<Ability> toRemove = new ArrayList<>();
        for (Ability destroyer : instances) {
            Class<? extends Ability> destroyerClass = destroyer.getClass();
            CollisionParticipant destroyerInfo = destroyerClass.getAnnotation(CollisionParticipant.class);
            List<Class<? extends Ability>> destroyerDestroyAbilities = Arrays.asList(destroyerInfo.destroyAbilities());
            if (destroyerDestroyAbilities.isEmpty()) continue;

            boolean isDestroyerDestroyed = false;

            for (Ability target : instances) {
                if (isDestroyerDestroyed) {
                    break;
                }
                if (destroyer.getUser().equals(target.getUser())) continue;

                Class<? extends Ability> targetClass = target.getClass();
                if (!destroyerDestroyAbilities.contains(targetClass)) continue;

                boolean isTargetDestroyed = false;

                CollisionParticipant targetInfo = targetClass.getAnnotation(CollisionParticipant.class);
                List<Class<? extends Ability>> targetDestroyAbilities = Arrays.asList(targetInfo.destroyAbilities());
                boolean collideDestroyer = targetDestroyAbilities.contains(destroyerClass);
                while (true) {
                    Collection<Collider> destroyerColliders = destroyer.getColliders();
                    if (destroyerColliders.isEmpty()) break;
                    Collection<Collider> targetColliders = target.getColliders();
                    if (targetColliders.isEmpty()) break;
                    int totalColliders = destroyerColliders.size() + targetColliders.size();
                    Map.Entry<Collider, Collider> collisionResult = checkCollision(destroyerColliders, targetColliders);
                    if (collisionResult == null) break;
                    if (target.destroyCollider(destroyer, collisionResult.getKey(), collisionResult.getValue()) == AbilityCollisionResult.DESTROY_INSTANCE) {
                        isTargetDestroyed = true;
                    }
                    if (collideDestroyer) {
                        if (destroyer.destroyCollider(target, collisionResult.getValue(), collisionResult.getKey()) == AbilityCollisionResult.DESTROY_INSTANCE) {
                            isDestroyerDestroyed = true;
                        }
                    }
                    if (isDestroyerDestroyed || isTargetDestroyed || totalColliders == destroyer.getColliders().size() + target.getColliders().size())
                        break;
                }
                if (isTargetDestroyed) {
                    toRemove.add(target);
                }
            }
            if (isDestroyerDestroyed) {
                toRemove.add(destroyer);
            }
        }
        toRemove.forEach(ability -> abilityInstanceService.destroyInstance(ability.getUser(), ability));
    }


    private Map.Entry<Collider, Collider> checkCollision(Collection<Collider> firstColliders, Collection<Collider> secondColliders) {
        for (Collider firstCollider : firstColliders) {
            if (firstCollider == null) continue;
            for (Collider secondCollider : secondColliders) {
                if (secondCollider == null) continue;
                if (firstCollider.intersects(secondCollider)) {
                    return Map.entry(firstCollider, secondCollider);
                }
            }
        }
        return null;
    }
}
