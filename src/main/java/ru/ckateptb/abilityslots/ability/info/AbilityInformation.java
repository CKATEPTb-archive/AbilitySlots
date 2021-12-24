package ru.ckateptb.abilityslots.ability.info;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.tablecloth.config.YamlConfigLoadEvent;
import ru.ckateptb.tablecloth.config.YamlConfigSaveEvent;
import ru.ckateptb.tablecloth.spring.SpringContext;

public interface AbilityInformation extends Listener {
    String getName();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    String getDisplayName();

    void setDisplayName(String displayName);

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
}
