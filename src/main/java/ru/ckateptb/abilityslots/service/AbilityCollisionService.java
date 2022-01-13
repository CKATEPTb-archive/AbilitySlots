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

import org.bukkit.Bukkit;
import org.spigotmc.AsyncCatcher;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.AbilitySlots;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.AbilityCollisionResult;
import ru.ckateptb.abilityslots.ability.info.CollisionParticipant;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.tablecloth.collision.Collider;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class AbilityCollisionService {
    private final AbilityInstanceService abilityInstanceService;
    private final AbilitySlotsConfig config;
    private final boolean canAsync;
    private boolean locked = false;

    public AbilityCollisionService(AbilityInstanceService abilityInstanceService, AbilitySlotsConfig config) {
        this.abilityInstanceService = abilityInstanceService;
        this.config = config;
        this.canAsync = AbilityInstanceService.isCanAsync();
    }

    @Scheduled(fixedRate = 1)
    public void update() {
        Collection<Ability> instances = abilityInstanceService.getInstances().stream()
                .filter(ability -> AnnotatedElementUtils.isAnnotated(ability.getClass(), CollisionParticipant.class))
                .filter(ability -> !ability.getColliders().isEmpty())
                .collect(Collectors.toList());
        if (canAsync && this.config.isAsyncCollisions()) updateAsync(new ArrayList<>(instances));
        else updateSync(instances);
    }

    private void updateAsync(Collection<Ability> instances) {
        if (locked) return;
        if (instances.isEmpty()) return;
        AbilitySlots plugin = AbilitySlots.getInstance();
        locked = true;
        List<CompletableFuture<List<Ability>>> futures = new ArrayList<>(instances).stream().map(destroyer -> CompletableFuture.supplyAsync(() -> {
            AsyncCatcher.enabled = false;
            return calculateDestroy(destroyer, instances);
        })).collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenRun(() -> {
            AsyncCatcher.enabled = true;
            locked = false;
            List<Ability> toRemove = new ArrayList<>();
            futures.stream()
                    .map(future -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    Bukkit.getScheduler().runTask(plugin, (Runnable) e::printStackTrace);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .forEach(toRemove::addAll);
            Bukkit.getScheduler().runTask(plugin, () -> toRemove.forEach(ability -> abilityInstanceService.destroyInstance(ability.getUser(), ability)));
        });
    }

    private void updateSync(Collection<Ability> instances) {
        List<Ability> toRemove = new ArrayList<>();
        for (Ability destroyer : instances) {
            toRemove.addAll(calculateDestroy(destroyer, instances));
        }
        toRemove.forEach(ability -> abilityInstanceService.destroyInstance(ability.getUser(), ability));
    }

    private List<Ability> calculateDestroy(Ability destroyer, Collection<Ability> instances) {
        List<Ability> toRemove = new ArrayList<>();
        Class<? extends Ability> destroyerClass = destroyer.getClass();
        CollisionParticipant destroyerInfo = destroyerClass.getAnnotation(CollisionParticipant.class);
        List<Class<? extends Ability>> destroyerDestroyAbilities = Arrays.asList(destroyerInfo.destroyAbilities());
        if (destroyerDestroyAbilities.isEmpty()) return toRemove;

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
                if (target.isDestroyed()) break;
                if (target.destroyCollider(destroyer, collisionResult.getKey(), collisionResult.getValue()) == AbilityCollisionResult.DESTROY_INSTANCE) {
                    isTargetDestroyed = true;
                }
                if (collideDestroyer) {
                    if (destroyer.isDestroyed()) break;
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
        return toRemove;
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
