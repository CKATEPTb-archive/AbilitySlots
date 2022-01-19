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

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;

public final class LWCProtection extends AbstractProtection {
    private final LWC lwc;

    public LWCProtection(Plugin plugin, AbilitySlotsConfig config) {
        super(config);
        lwc = ((LWCPlugin) plugin).getLWC();
    }

    @Override
    public boolean canUse(LivingEntity entity, Location location) {
        if (!config.isProtectionLWC()) return true;
        if (entity instanceof Player player) {
            com.griefcraft.model.Protection protection = lwc.getProtectionCache().getProtection(location.getBlock());
            return protection == null || lwc.canAccessProtection(player, protection);
        }
        return true;
    }
}
