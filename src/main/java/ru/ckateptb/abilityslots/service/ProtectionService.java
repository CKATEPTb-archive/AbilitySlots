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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.entity.AbilityTargetLiving;
import ru.ckateptb.abilityslots.protection.*;
import ru.ckateptb.tablecloth.cache.Cache;
import ru.ckateptb.tablecloth.cache.Caffeine;
import ru.ckateptb.tablecloth.ioc.annotation.Autowired;
import ru.ckateptb.tablecloth.ioc.annotation.Component;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Getter
@Component
public class ProtectionService implements Listener, Iterable<Protection> {
    private final AbilitySlotsConfig config;
    private final Set<Protection> protectionPlugins = new HashSet<>();
    // For optimize caching, we must use Block, because its location is always static without yaw and pitch
    private final Map<UUID, Cache<BlockPos, Boolean>> cache = new HashMap<>();

    @Autowired
    public ProtectionService(AbilitySlotsConfig config) {
        this.config = config;
        register("WorldGuard", plugin -> new WorldGuardProtection(plugin, config));
        register("GriefPrevention", plugin -> new GriefPreventionProtection(plugin, config));
        register("Towny", plugin -> new TownyProtection(plugin, config));
        register("LWC", plugin -> new LWCProtection(plugin, config));
    }

    private void register(String name, Function<Plugin, Protection> factory) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (plugin != null) {
            Protection protection = factory.apply(plugin);
            protectionPlugins.add(protection);
            log.info("Registered protection for " + name);
        }
    }

    public boolean canUse(AbilityTargetLiving user, Location location) {
        LivingEntity entity = user.getEntity();
        UUID uuid = entity.getUniqueId();
        return cache.computeIfAbsent(uuid, key ->
                Caffeine.newBuilder().expireAfterAccess(Duration.ofMillis(config.getProtectionCacheDuration())).build()
        ).get(new BlockPos(location), loc -> this.stream().allMatch(protection -> protection.canUse(entity, location)));
    }

    @EventHandler
    public void on(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) return;
        cache.remove(entity.getUniqueId());
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        cache.remove(event.getPlayer().getUniqueId());
    }

    public Stream<Protection> stream() {
        return protectionPlugins.stream();
    }

    @Override
    public Iterator<Protection> iterator() {
        return Collections.unmodifiableCollection(protectionPlugins).iterator();
    }

    private static class BlockPos {
        private final int x;
        private final int y;
        private final int z;
        private final World world;

        public BlockPos(Location location) {
            this.world = location.getWorld();
            this.x = location.getBlockX();
            this.y = location.getBlockY();
            this.z = location.getBlockZ();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (!(obj instanceof BlockPos other)) return false;
            return this.x == other.x && this.y == other.y && this.z == other.z && this.world.equals(other.world);
        }

        @Override
        public int hashCode() {
            return ((this.y + this.z * 31) * 31 + this.x) ^ this.world.hashCode();
        }
    }
}
