package ru.ckateptb.abilityslots.ability.info;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.abilityslots.util.TimeUtil;

import java.util.Arrays;

@Getter
@Setter
public class AnnotationBasedAbilityInformation implements AbilityInformation {
    private final String name;
    private final Class<? extends Ability> abilityClass;
    private final AbilityCategory category;
    private final String author;
    private final ActivationMethod[] activationMethods;
    private long cooldown;
    private int cost;
    private boolean enabled;
    private String displayName;
    private String description;
    private String instruction;
    private boolean canBindToSlot;

    public AnnotationBasedAbilityInformation(AbilityInfo abilityInfo, AbilityCategory abilityCategory, Class<? extends Ability> abilityClass) {
        this.name = abilityInfo.name();
        this.abilityClass = abilityClass;
        this.category = abilityCategory;
        this.displayName = abilityInfo.displayName();
        this.description = abilityInfo.description();
        this.instruction = abilityInfo.instruction();
        this.cooldown = abilityInfo.cooldown();
        this.cost = abilityInfo.cost();
        this.enabled = true;
        this.canBindToSlot = abilityInfo.canBindToSlot();
        this.author = abilityInfo.author();
        this.activationMethods = abilityInfo.activationMethods();
    }

    @Override
    public boolean isActivatedBy(ActivationMethod method) {
        return Arrays.asList(this.activationMethods).contains(method);
    }

    @Override
    public String getFormattedName() {
        return getFormattedNameForUser(null);
    }

    @Override
    public String getFormattedNameForUser(AbilityUser user) {
        StringBuilder builder = new StringBuilder();
        String prefix = this.category.getPrefix();
        builder.append(prefix);
        if (user != null && user.hasCooldown(this)) {
            long cooldown = user.getCooldowns().get(this) - System.currentTimeMillis();
            return builder
                    .append(ChatColor.STRIKETHROUGH)
                    .append(this.getDisplayName())
                    .append(ChatColor.RESET)
                    .append(prefix)
                    .append(" - ")
                    .append(TimeUtil.formatTime(cooldown))
                    .toString();
        }
        return builder.append(this.displayName).toString();
    }

    @Override
    @SneakyThrows
    public Ability createAbility() {
        return abilityClass.getConstructor().newInstance();
    }
}
