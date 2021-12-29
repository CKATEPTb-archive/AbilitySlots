package ru.ckateptb.abilityslots.service;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.AbilityCollisionResult;
import ru.ckateptb.abilityslots.ability.info.DestroyAbilities;
import ru.ckateptb.tablecloth.collision.Collider;

import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class CollisionService {
    private final AbilityInstanceService abilityInstanceService;

    public CollisionService(AbilityInstanceService abilityInstanceService) {
        this.abilityInstanceService = abilityInstanceService;
    }

    @Scheduled(fixedRate = 1)
    public void update() {
        Collection<Ability> instances = abilityInstanceService.getInstances().stream()
                .filter(ability -> AnnotatedElementUtils.isAnnotated(ability.getClass(), DestroyAbilities.class))
                .filter(ability -> !ability.getColliders().isEmpty())
                .collect(Collectors.toList());
        List<Ability> toRemove = new ArrayList<>();
        for (Ability destroyer : instances) {
            Class<? extends Ability> destroyerClass = destroyer.getClass();
            DestroyAbilities destroyerInfo = destroyerClass.getAnnotation(DestroyAbilities.class);
            List<Class<? extends Ability>> destroyerDestroyAbilities = Arrays.asList(destroyerInfo.destroyAbilities());
            if (destroyerDestroyAbilities.isEmpty()) continue;

            boolean isDestroyerDestroyed = false;

            for (Ability target : instances) {
                if(isDestroyerDestroyed) {
                    break;
                }
                if (destroyer.getUser().equals(target.getUser())) continue;

                Class<? extends Ability> targetClass = target.getClass();
                if (!destroyerDestroyAbilities.contains(targetClass)) continue;

                boolean isTargetDestroyed = false;

                DestroyAbilities targetInfo = targetClass.getAnnotation(DestroyAbilities.class);
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
            if(isDestroyerDestroyed) {
                toRemove.add(destroyer);
            }
        }
        toRemove.forEach(ability -> abilityInstanceService.destroyInstance(ability.getUser(), ability));
    }


    private Map.Entry<Collider, Collider> checkCollision(Collection<Collider> firstColliders, Collection<Collider> secondColliders) {
        for (Collider firstCollider : firstColliders) {
            for (Collider secondCollider : secondColliders) {
                if (firstCollider.intersects(secondCollider)) {
                    return Map.entry(firstCollider, secondCollider);
                }
            }
        }
        return null;
    }
}
