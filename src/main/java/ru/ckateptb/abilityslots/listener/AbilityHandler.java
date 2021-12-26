package ru.ckateptb.abilityslots.listener;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.springframework.stereotype.Component;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivateResult;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.service.AbilityInstanceService;
import ru.ckateptb.abilityslots.service.AbilityUserService;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

@Component
public class AbilityHandler implements Listener {
    private final AbilityUserService userService;
    private final AbilityInstanceService abilityInstanceService;

    public AbilityHandler(AbilityUserService userService, AbilityInstanceService abilityInstanceService) {
        this.userService = userService;
        this.abilityInstanceService = abilityInstanceService;
    }

    public ActivateResult activateAbility(AbilityUser abilityUser, ActivationMethod method) {
        if (!(abilityUser instanceof PlayerAbilityUser user)) return ActivateResult.NOT_ACTIVATE;
        AbilityInformation ability = user.getSelectedAbility();

        if (ability == null
                || !ability.isActivatedBy(method)
                || !user.canActivate(ability)) return ActivateResult.NOT_ACTIVATE;

        Ability instance = ability.createAbility();
        ActivateResult activateResult = instance.activate(user, method);
        if (activateResult == ActivateResult.ACTIVATE || activateResult == ActivateResult.ACTIVATE_AND_CANCEL_EVENT) {
            abilityInstanceService.registerInstance(user, instance);
        }

        return activateResult;
    }

    @EventHandler(ignoreCancelled = true)
    public void onLeftClick(PlayerAnimationEvent event) {
        PlayerAbilityUser user = userService.getAbilityPlayer(event.getPlayer());
        if (user == null) return;
        ActivateResult result = activateAbility(user, ActivationMethod.LEFT_CLICK);
        if(result == ActivateResult.NOT_ACTIVATE_CANCEL_EVENT || result == ActivateResult.ACTIVATE_AND_CANCEL_EVENT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent event) {
        AbilityUser user = userService.getAbilityPlayer(event.getPlayer());
        if (user == null) return;
        ActivateResult result = activateAbility(user, event.isSneaking() ? ActivationMethod.SNEAK : ActivationMethod.SNEAK_RELEASE);
        if(result == ActivateResult.NOT_ACTIVATE_CANCEL_EVENT || result == ActivateResult.ACTIVATE_AND_CANCEL_EVENT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onFall(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        AbilityUser user = userService.getAbilityUser(livingEntity);
        if (user == null) return;
        ActivateResult result = activateAbility(user, ActivationMethod.FALL);
        if(result == ActivateResult.NOT_ACTIVATE_CANCEL_EVENT || result == ActivateResult.ACTIVATE_AND_CANCEL_EVENT) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        AbilityUser user = userService.getAbilityPlayer(event.getPlayer());
        if (user == null) return;
        if (event.getHand() == EquipmentSlot.HAND) {
            Action action = event.getAction();
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                ActivateResult result = activateAbility(user, ActivationMethod.RIGHT_CLICK);
                if(result == ActivateResult.NOT_ACTIVATE_CANCEL_EVENT || result == ActivateResult.ACTIVATE_AND_CANCEL_EVENT) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
