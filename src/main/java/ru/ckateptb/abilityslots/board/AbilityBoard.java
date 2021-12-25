package ru.ckateptb.abilityslots.board;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;

import java.util.*;

@Getter
public class AbilityBoard {
    private final AbilitySlotsConfig config;
    private final AbilityService abilityService;
    private final ScoreboardManager scoreboardManager = Objects.requireNonNull(Bukkit.getScoreboardManager());

    private final PlayerAbilityUser user;
    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final Set<String> updatedScores = new HashSet<>();
    @Setter
    private boolean enabled;

    public AbilityBoard(PlayerAbilityUser user, AbilitySlotsConfig config, AbilityService abilityService) {
        this.config = config;
        this.abilityService = abilityService;
        this.user = user;
        this.player = user.getEntity();
        this.scoreboard = scoreboardManager.getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("abilityboard", "dummy", config.getBoardHeader(), RenderType.INTEGER);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void update() {
        if (player == null || !player.isOnline()) return;
        if (!enabled || !config.isBoardEnabled() || !player.hasPermission("abilityslots.board.display")) {
            if (player.getScoreboard() == scoreboard) {
                player.setScoreboard(scoreboardManager.getMainScoreboard());
            }
            return;
        }
        if (player.getScoreboard() != scoreboard) {
            player.setScoreboard(scoreboard);
        }
        updatedScores.clear();

        updateSlots();
        updateSequences();

        // Clear out any scores that aren't needed.
        for (String entry : scoreboard.getEntries()) {
            if (updatedScores.contains(entry)) continue;
            scoreboard.resetScores(entry);
        }
    }

    private void updateSlots() {
        int currentSlot = user.getHeldItemSlot();

        for (int slotIndex = 1; slotIndex <= 9; ++slotIndex) {
            StringBuilder sb = new StringBuilder();
            AbilityInformation ability = user.getSlotContainer().getAbility(slotIndex);

            sb.append(getUniquePrefix(slotIndex));

            if (slotIndex == currentSlot) {
                sb.append(">");
            }

            if (ability == null) {
                sb.append(config.getBoardEmptySlot());
            } else {
                sb.append(ability.getFormattedNameForUser(user));
            }

            updatedScores.add(sb.toString());

            Score score = objective.getScore(sb.toString());
            // Only set the new score if it changes.
            if (score.getScore() != -slotIndex) {
                score.setScore(-slotIndex);
            }
        }
    }

    private void updateSequences() {
        int slotIndex = 10;
        List<String> updates = new ArrayList<>();
        for (AbilityInformation ability : abilityService.getAbilities()) {
            if (ability.isActivatedBy(ActivationMethod.SEQUENCE)) {
                if (user.hasCooldown(ability)) {
                    if (updates.isEmpty()) {
                        updates.add(ChatColor.BOLD + "Sequences");
                    }
                    updates.add(getUniquePrefix(slotIndex) + ability.getFormattedNameForUser(user));
                }
            }
        }

        for (String update : updates) {
            Score score = objective.getScore(update);

            if (score.getScore() != -slotIndex) {
                score.setScore(-slotIndex);
            }

            updatedScores.add(update);
            ++slotIndex;
        }
    }

    // Make sure each entry has its own unique string.
    // This makes it so it doesn't remove duplicate binds.
    private String getUniquePrefix(int index) {
        if (index < 22) {
            return ChatColor.values()[index].toString() + ChatColor.RESET;
        }

        return ChatColor.RESET + getUniquePrefix(index - 22);
    }

}
