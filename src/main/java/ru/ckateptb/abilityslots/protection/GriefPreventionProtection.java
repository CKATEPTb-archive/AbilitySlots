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

package ru.ckateptb.abilityslots.protection;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;

public final class GriefPreventionProtection extends AbstractProtection {
    private final GriefPrevention griefPrevention;

    public GriefPreventionProtection(Plugin plugin, AbilitySlotsConfig config) {
        super(config);
        griefPrevention = (GriefPrevention) plugin;
    }

    @Override
    public boolean canUse(LivingEntity entity, Location location) {
        if(!config.isProtectionGriefPrevention()) return true;
        if (entity instanceof Player player) {
            String reason = griefPrevention.allowBuild(player, location);
            Claim claim = griefPrevention.dataStore.getClaimAt(location, true, null);
            return reason == null || claim == null || claim.siegeData != null;
        }
        return true;
    }
}
