package ru.ckateptb.abilityslots.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.springframework.stereotype.Component;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivateResult;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.enums.SequenceAction;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.service.AbilityInstanceService;
import ru.ckateptb.abilityslots.service.AbilitySequenceService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.service.AbilityUserService;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;
import ru.ckateptb.tablecloth.collision.RayTrace;

import java.util.ArrayList;
import java.util.List;

@Component
public class AbilityHandler implements Listener {
    private final AbilityUserService userService;
    private final AbilityInstanceService abilityInstanceService;
    private final AbilitySequenceService abilitySequenceService;
    private final AbilityService abilityService;

    public AbilityHandler(AbilityUserService userService, AbilityService abilityService, AbilityInstanceService abilityInstanceService, AbilitySequenceService abilitySequenceService) {
        this.userService = userService;
        this.abilityService = abilityService;
        this.abilityInstanceService = abilityInstanceService;
        this.abilitySequenceService = abilitySequenceService;
    }

    public ActivateResult activateAbility(AbilityUser abilityUser, AbilityInformation ability, ActivationMethod method) {
        if (!(abilityUser instanceof PlayerAbilityUser user)) return ActivateResult.NOT_ACTIVATE;

        if (ability == null
                || !ability.isActivatedBy(method)
                || !user.canActivate(ability)
        ) return ActivateResult.NOT_ACTIVATE;

        Ability instance = ability.createAbility();
        ActivateResult activateResult = instance.activate(user, method);
        if (isActivate(activateResult)) {
            abilityInstanceService.registerInstance(user, instance);
        }

        return activateResult;
    }

    public List<AbilityInformation> getHandledAbilities(AbilityUser user) {
        List<AbilityInformation> list = new ArrayList<>(abilityService.getPassiveAbilities());
        list.addAll(abilityService.getDamageAbilities());
        list.removeIf(passive -> !abilityInstanceService.hasAbility(user, passive));
        AbilityInformation selectedAbility = user.getSelectedAbility();
        list.add(selectedAbility);
        return list;
    }

    @EventHandler
    public void onLeftClick(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        PlayerAbilityUser user = userService.getAbilityPlayer(player);
        if (user == null) return;
        RayTrace.CompositeResult trace = RayTrace.of(player).range(5).type(RayTrace.Type.ENTITY).filter(e -> e instanceof LivingEntity && e != player).result(player.getWorld());
        if (trace.entity() != null) {
            if (isActivate(abilitySequenceService.registerAction(user, SequenceAction.LEFT_CLICK_ENTITY))) {
                return;
            }
        } else if (trace.block() != null) {
            if (isActivate(abilitySequenceService.registerAction(user, SequenceAction.LEFT_CLICK_BLOCK))) {
                return;
            }
        } else {
            if (isActivate(abilitySequenceService.registerAction(user, SequenceAction.LEFT_CLICK))) {
                return;
            }
        }

        this.getHandledAbilities(user).forEach(ability -> {
            ActivateResult result = activateAbility(user, ability, ActivationMethod.LEFT_CLICK);
            if (shouldCancelEvent(result)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        AbilityUser user = userService.getAbilityPlayer(event.getPlayer());
        if (user == null) return;

        boolean sneaking = event.isSneaking();
        if (isActivate(abilitySequenceService.registerAction(user, sneaking ? SequenceAction.SNEAK : SequenceAction.SNEAK_RELEASE))) {
            return;
        }

        this.getHandledAbilities(user).forEach(ability -> {
            ActivateResult result = activateAbility(user, ability, sneaking ? ActivationMethod.SNEAK : ActivationMethod.SNEAK_RELEASE);
            if (shouldCancelEvent(result)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        AbilityUser user = userService.getAbilityUser(livingEntity);
        if (user == null) return;
        this.getHandledAbilities(user).forEach(ability -> {
            ActivateResult result = activateAbility(user, ability, ActivationMethod.FALL);
            if (shouldCancelEvent(result)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onFall(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        AbilityUser user = userService.getAbilityUser(livingEntity);
        if (user == null) return;
        this.getHandledAbilities(user).forEach(ability -> {
            ActivateResult result = activateAbility(user, ability, ActivationMethod.FALL);
            if (shouldCancelEvent(result)) {
                event.setCancelled(true);
            }
        });
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        AbilityUser user = userService.getAbilityPlayer(player);
        if (user == null) return;
        if (event.getHand() == EquipmentSlot.HAND) {
            Action action = event.getAction();
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                // RIGHT_CLICK_AIR doesn't work in 1.17.1
                if (isActivate(abilitySequenceService.registerAction(user, action == Action.RIGHT_CLICK_AIR ? SequenceAction.RIGHT_CLICK : SequenceAction.RIGHT_CLICK_BLOCK))) {
                    return;
                }
                this.getHandledAbilities(user).forEach(ability -> {
                    ActivateResult result = activateAbility(user, ability, ActivationMethod.RIGHT_CLICK);
                    if (shouldCancelEvent(result)) {
                        event.setCancelled(true);
                    }
                });
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void on(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        AbilityUser user = userService.getAbilityPlayer(player);
        if (user == null || event.getHand() != EquipmentSlot.HAND) return;
        abilitySequenceService.registerAction(user, SequenceAction.RIGHT_CLICK_ENTITY);
    }

    private boolean isActivate(ActivateResult result) {
        return result == ActivateResult.ACTIVATE || result == ActivateResult.ACTIVATE_AND_CANCEL_EVENT;
    }

    private boolean shouldCancelEvent(ActivateResult result) {
        return result == ActivateResult.NOT_ACTIVATE_AND_CANCEL_EVENT || result == ActivateResult.ACTIVATE_AND_CANCEL_EVENT;
    }
}
