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

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import lombok.SneakyThrows;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivateResult;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.config.AbilityCastPreventType;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;
import ru.ckateptb.abilityslots.storage.AbilitySlotsStorage;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;
import ru.ckateptb.tablecloth.async.AsyncService;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@EnableScheduling
public class AbilityUserService implements Listener {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final Map<LivingEntity, AbilityUser> users = new HashMap<>();
    private final AbilitySlotsStorage storage;
    private final AbilitySlotsConfig config;
    private final AbilityService abilityService;
    private final AbilityInstanceService abilityInstanceService;
    private final AsyncService asyncService;

    public AbilityUserService(AbilitySlotsStorage storage, AbilitySlotsConfig config, AbilityService abilityService, AbilityInstanceService abilityInstanceService, AsyncService asyncService) {
        this.storage = storage;
        this.config = config;
        this.abilityService = abilityService;
        this.abilityInstanceService = abilityInstanceService;
        this.asyncService = asyncService;
    }

    public PlayerAbilityUser getAbilityPlayer(Player player) {
        AbilityUser abilityUser = users.get(player);
        if (abilityUser == null) {
            PlayerAbilityUser user = new PlayerAbilityUser(player, config, abilityService, abilityInstanceService, storage, asyncService);
            user.enableAbilityBoard();
            user.enableEnergyBar();
            users.put(player, user);
            return user;
        }
        return (PlayerAbilityUser) abilityUser;
    }

    public AbilityUser getAbilityUser(LivingEntity livingEntity) {
        return users.get(livingEntity);
    }

    @Scheduled(initialDelay = 5, fixedRate = 1)
    public void update() {
        CompletableFuture.runAsync(() -> new CopyOnWriteArrayList<>(users.values()).forEach(abilityUser -> {
            if (abilityUser instanceof PlayerAbilityUser user) {
                user.updateAbilityBoard();
                user.updateEnergyBar();
            }
        }), executorService);
    }

    @Scheduled(fixedRate = 20)
    public void updatePassives() {
        Collection<AbilityInformation> passives = abilityService.getPassiveAbilities();
        for (AbilityInformation passive : passives) {
            for (AbilityUser user : users.values()) {
                if (passive.isEnabled() && user.canActivate(passive)) {
                    if (!abilityInstanceService.hasAbility(user, passive)) {
                        Ability ability = passive.createAbility();
                        ability.setUser(user);
                        ActivateResult activateResult = ability.activate(ActivationMethod.PASSIVE);
                        if (activateResult == ActivateResult.ACTIVATE || activateResult == ActivateResult.ACTIVATE_AND_CANCEL_EVENT) {
                            abilityInstanceService.registerInstance(user, ability);
                        }
                    }
                } else if (abilityInstanceService.hasAbility(user, passive)) {
                    abilityInstanceService.destroyInstanceType(user, passive);
                }
            }
        }
    }

    @Scheduled(fixedRate = 20)
    public void regenEnergy() {
        if(config.getCastPreventType() == AbilityCastPreventType.COOLDOWN) return;
        for (AbilityUser user : users.values()) {
            user.addEnergy(config.getEnergyRegen());
        }
    }

    @SneakyThrows
    @EventHandler
    public void on(PlayerJoinEvent event) {
        getAbilityPlayer(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        AbilityUser user = users.remove(event.getPlayer());
        if (user != null) {
            abilityInstanceService.destroyAbilityUserInstances(user);
        }
    }

    @EventHandler
    public void on(EntityRemoveFromWorldEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            if (!(entity instanceof Player)) {
                AbilityUser user = users.remove(entity);
                if (user != null) {
                    abilityInstanceService.destroyAbilityUserInstances(user);
                }
            }
        }
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            AbilityUser user = users.remove(entity);
            if (user != null) {
                abilityInstanceService.destroyAbilityUserInstances(user);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void on(AbilitySlotsReloadEvent event) {
        this.users.values().forEach(abilityUser -> {
            if (abilityUser instanceof PlayerAbilityUser user) {
                user.getEnergyBar().getBossBar().removeAll();
            }
        });
        this.users.clear();
    }
}
