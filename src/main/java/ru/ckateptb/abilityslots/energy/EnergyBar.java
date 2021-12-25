package ru.ckateptb.abilityslots.energy;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import ru.ckateptb.abilityslots.config.AbilityCastPreventType;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

@Getter
public class EnergyBar {
    private final AbilitySlotsConfig config;
    private final BossBar bossBar;
    private final PlayerAbilityUser user;
    private final Player player;
    @Setter
    private boolean enabled;

    public EnergyBar(PlayerAbilityUser user, AbilitySlotsConfig config) {
        this.config = config;
        this.user = user;
        this.player = user.getEntity();
        this.bossBar = Bukkit.createBossBar(config.getEnergyName(), config.getEnergyColor(), config.getEnergyStyle());
    }

    public void update() {
        if (player == null || !player.isOnline()) return;
        if (!enabled || config.getCastPreventType() == AbilityCastPreventType.COOLDOWN || !player.hasPermission("abilityslots.energybar.display")) {
            if (this.bossBar.getPlayers().contains(player)) {
                this.bossBar.removePlayer(player);
            }
            return;
        }
        if (!this.bossBar.getPlayers().contains(player)) {
            this.bossBar.addPlayer(player);
        }
        double progress = user.getEnergy() / user.getMaxEnergy();
        if (this.bossBar.getProgress() != progress) {
            this.bossBar.setProgress(progress);
        }
    }
}
