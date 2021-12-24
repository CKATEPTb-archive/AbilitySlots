package ru.ckateptb.abilityslots.board;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.user.PlayerAbilityUser;
import ru.ckateptb.abilityslots.util.TimeUtil;

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
        if(player == null || !player.isOnline()) return;
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
                sb.append(ability.getCategory().getPrefix());
                if (user.hasCooldown(ability)) {
                    long cooldown = user.getCooldowns().get(ability) - System.currentTimeMillis();
                    sb.append(ChatColor.STRIKETHROUGH)
                            .append(ability.getDisplayName())
                            .append(ChatColor.RESET)
                            .append(ability.getCategory().getPrefix())
                            .append(" - ")
                            .append(TimeUtil.formatTime(cooldown));
                } else {
                sb.append(ability.getDisplayName());
                }
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
                    String name = getUniquePrefix(slotIndex);

                    long cooldown = user.getCooldowns().get(ability) - System.currentTimeMillis();
                    name += ability.getCategory().getPrefix();
                    name += ChatColor.STRIKETHROUGH;
                    name += ability.getDisplayName();
                    name += (ChatColor.RESET);
                    name += ability.getCategory().getPrefix();
                    name += (" - ");
                    name += (TimeUtil.formatTime(cooldown));

                    if (updates.isEmpty()) {
                        updates.add(ChatColor.BOLD + "Sequences");
                    }

                    updates.add(name);
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
