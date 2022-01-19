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

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;

public final class TownyProtection extends AbstractProtection {
    private final TownyAPI api;

    public TownyProtection(Plugin plugin, AbilitySlotsConfig config) {
        super(config);
        api = TownyAPI.getInstance();
    }

    @Override
    public boolean canUse(LivingEntity entity, Location location) {
        if (!config.isProtectionTowny()) return true;
        if (entity instanceof Player player) {
            return PlayerCacheUtil.getCachePermission(player, location, Material.DIRT, TownyPermission.ActionType.BUILD);
        }
        TownBlock townBlock = api.getTownBlock(location);
        return townBlock == null || !townBlock.hasTown();
    }
}
