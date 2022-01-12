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

package ru.ckateptb.abilityslots.ability.conditional;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

import java.util.Arrays;
import java.util.List;

public class GameModeAbilityConditional implements AbilityConditional {
    private final List<GameMode> restricted;

    public GameModeAbilityConditional(GameMode... restricted) {
        this.restricted = Arrays.asList(restricted);
    }

    @Override
    public boolean matches(AbilityUser user, AbilityInformation ability) {
        if (user instanceof PlayerAbilityUser playerAbilityUser) {
            Player player = playerAbilityUser.getEntity();
            return !restricted.contains(player.getGameMode());
        }
        return true;
    }
}
