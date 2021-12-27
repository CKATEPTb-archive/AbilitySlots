package ru.ckateptb.abilityslots.ability.info;

import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.user.AbilityUser;
import ru.ckateptb.tablecloth.config.YamlConfigLoadEvent;
import ru.ckateptb.tablecloth.config.YamlConfigSaveEvent;
import ru.ckateptb.tablecloth.minedown.MineDown;
import ru.ckateptb.tablecloth.spring.SpringContext;

public interface AbilityInformation extends Listener {
    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getDisplayName();

    void setDisplayName(String displayName);

    String getFormattedName();

    String getFormattedNameForUser(AbilityUser user);

    String getDescription();

    void setDescription(String description);

    String getInstruction();

    void setInstruction(String instruction);

    long getCooldown();

    void setCooldown(long cooldown);

    int getCost();

    void setCost(int cost);

    boolean isActivatedBy(ActivationMethod method);

    ActivationMethod[] getActivationMethods();

    boolean isCanBindToSlot();

    String getAuthor();

    AbilityCategory getCategory();

    Ability createAbility();

    Class<? extends Ability> getAbilityClass();

    @SneakyThrows
    @EventHandler
    default void on(YamlConfigLoadEvent event) {
        if (event.getYamlConfig() != SpringContext.getInstance().getBean(AbilitySlotsConfig.class)) return;
        YamlConfiguration config = event.getBukkitConfig();
        setEnabled(config.getBoolean(getConfigPath("enabled"), isEnabled()));
        setDisplayName(config.getString(getConfigPath("name"), getDisplayName()));
        setDescription(config.getString(getConfigPath("description"), getDescription()));
        if (!(isActivatedBy(ActivationMethod.PASSIVE) && getActivationMethods().length == 1)) {
            setInstruction(config.getString(getConfigPath("instruction"), getInstruction()));
            setCooldown(config.getLong(getConfigPath("cooldown"), getCooldown()));
            setCost(config.getInt(getConfigPath("cost"), getCost()));
        }
        event.scan(getAbilityClass(), null, this::getConfigPath);
    }

    @SneakyThrows
    @EventHandler
    default void on(YamlConfigSaveEvent event) {
        if (event.getYamlConfig() != SpringContext.getInstance().getBean(AbilitySlotsConfig.class)) return;
        event.set(getConfigPath("enabled"), isEnabled());
        event.set(getConfigPath("name"), getDisplayName());
        event.set(getConfigPath("description"), getDescription());
        if (!(isActivatedBy(ActivationMethod.PASSIVE) && getActivationMethods().length == 1)) {
            event.set(getConfigPath("instruction"), getInstruction());
            event.set(getConfigPath("cooldown"), getCooldown());
            event.set(getConfigPath("cost"), getCost());
        }
        event.scan(getAbilityClass(), null, this::getConfigPath);
    }

    default String getConfigPath(String field) {
        return "abilities." + getCategory().getName() + "." + getName() + "." + field;
    }

    default BaseComponent[] toBaseComponent() {
        AbilitySlotsConfig config = SpringContext.getInstance().getBean(AbilitySlotsConfig.class);
        AbilityCategory category = getCategory();
        String prefix = config.isRespectCategoryPrefix() ? category.getPrefix() : "";
        StringBuilder builder = new StringBuilder();
        if (!isActivatedBy(ActivationMethod.PASSIVE) && !isActivatedBy(ActivationMethod.SEQUENCE)) {
            builder
                    .append(config.getBindToSlotText())
                    .append(prefix)
                    .append("[①](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 1)")
                    .append("[②](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 2)")
                    .append("[③](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 3)")
                    .append("[④](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 4)")
                    .append("[⑤](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 5)")
                    .append("[⑥](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 6)")
                    .append("[⑦](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 7)")
                    .append("[⑧](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 8)")
                    .append("[⑨](run_command=/abilityslots bind ")
                    .append(getName())
                    .append(" 9) - ");
        }
        builder
                .append("[")
                .append(getFormattedName())
                .append("]")
                .append("(")
                .append("hover=")
                .append(ChatColor.RESET)
                .append(config.getAuthorText())
                .append(getAuthor())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getCategoryText())
                .append(prefix)
                .append(category.getDisplayName())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getDescriptionText())
                .append(prefix)
                .append(getDescription())
                .append("\n")
                .append(ChatColor.RESET)
                .append(config.getInstructionText())
                .append(prefix)
                .append(getInstruction())
                .append(")");
        return MineDown.parse(builder.toString());
    }
}
