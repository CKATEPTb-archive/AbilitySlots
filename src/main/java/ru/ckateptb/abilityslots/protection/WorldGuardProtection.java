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

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;

@Slf4j
public final class WorldGuardProtection extends AbstractProtection {
    private final WorldGuard worldGuard;
    private StateFlag flag;

    public WorldGuardProtection(Plugin plugin, AbilitySlotsConfig config) {
        super(config);
        this.worldGuard = WorldGuard.getInstance();
        FlagRegistry registry = worldGuard.getFlagRegistry();
        try {
            this.flag = new StateFlag("abilityslots", false);
            registry.register(flag);
        } catch (FlagConflictException e) {
            this.flag = Flags.BUILD;
            log.warn("Failed to register AbilitySlots flag for WorldGuard. Use the build flag");
            e.printStackTrace();
        }
    }

    @Override
    public boolean canUse(LivingEntity entity, Location location) {
        if (!config.isProtectionWorldGuard()) return true;
        RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location worldGuardLocation = BukkitAdapter.adapt(location);
        if (entity instanceof Player player) {
            LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            World world = BukkitAdapter.adapt(location.getWorld());
            if (worldGuard.getPlatform().getSessionManager().hasBypass(localPlayer, world)) {
                return true;
            }
            return query.testState(worldGuardLocation, localPlayer, flag);
        }
        // Query WorldGuard to see if a non-member (entity) can build in a region.
        return query.testState(worldGuardLocation, list -> Association.NON_MEMBER, flag);
    }
}
