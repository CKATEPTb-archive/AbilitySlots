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
