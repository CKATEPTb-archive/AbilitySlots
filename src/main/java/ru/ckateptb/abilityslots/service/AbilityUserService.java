package ru.ckateptb.abilityslots.service;

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import lombok.SneakyThrows;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.storage.AbilitySlotsStorage;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;
import ru.ckateptb.tablecloth.async.AsyncService;

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

    public AbilityUserService(AbilitySlotsStorage storage, AbilitySlotsConfig config, AbilityService abilityService, AbilityInstanceService abilityInstanceService) {
        this.storage = storage;
        this.config = config;
        this.abilityService = abilityService;
        this.abilityInstanceService = abilityInstanceService;
    }

    public PlayerAbilityUser getAbilityPlayer(Player player) {
        AbilityUser abilityUser = users.get(player);
        if (abilityUser == null) return null;
        return (PlayerAbilityUser) abilityUser;
    }

    public AbilityUser getAbilityUser(LivingEntity livingEntity) {
        return users.get(livingEntity);
    }

    @Scheduled(initialDelay = 5, fixedRate = 1)
    public void update() {
        CompletableFuture.runAsync(() -> {
            new CopyOnWriteArrayList<>(users.values()).forEach(abilityUser -> {
                if (abilityUser instanceof PlayerAbilityUser user) {
                    user.updateAbilityBoard();
                    user.updateEnergyBar();
                }
            });
        }, executorService);
    }

    @SneakyThrows
    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerAbilityUser user = new PlayerAbilityUser(player, config, abilityService, abilityInstanceService);
        user.enableAbilityBoard();
        user.enableEnergyBar();
        users.put(player, user);
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        users.remove(event.getPlayer());
    }

    @EventHandler
    public void on(EntityRemoveFromWorldEvent event) {
        if (event.getEntity() instanceof LivingEntity entity) {
            if (!(entity instanceof Player)) {
                users.remove(entity);
            }
        }
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            users.remove(entity);
        }
    }
}
