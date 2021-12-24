package ru.ckateptb.abilityslots.ability.conditional;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

import java.util.Arrays;
import java.util.List;

public class GameModeAbilityActivateConditional implements AbilityActivateConditional {
    private final List<GameMode> restricted;

    public GameModeAbilityActivateConditional(GameMode... restricted) {
        this.restricted = Arrays.asList(restricted);
    }

    @Override
    public boolean canActivate(AbilityUser user, AbilityInformation ability) {
        if (user instanceof PlayerAbilityUser playerAbilityUser) {
            Player player = playerAbilityUser.getEntity();
            return !restricted.contains(player.getGameMode());
        }
        return true;
    }
}
