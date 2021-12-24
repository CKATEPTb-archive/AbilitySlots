package ru.ckateptb.abilityslots.user;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.springframework.stereotype.Service;

@Service
public class AbilityUserService implements Listener {
    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
    }
}
