package ru.ckateptb.abilityslots.category;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.tablecloth.config.YamlConfigLoadEvent;
import ru.ckateptb.tablecloth.config.YamlConfigSaveEvent;
import ru.ckateptb.tablecloth.spring.SpringContext;

import java.util.Collection;

public interface AbilityCategory extends Listener {
    String getName();

    String getDisplayName();

    void setDisplayName(String displayName);

    String getPrefix();

    void setPrefix(String prefix);

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Collection<AbilityInformation> getAbilities();

    AbilityInformation getAbility(String name);

    void registerAbility(AbilityInformation ability);

    @SneakyThrows
    @EventHandler
    default void on(YamlConfigLoadEvent event) {
        if (event.getYamlConfig() != SpringContext.getInstance().getBean(AbilitySlotsConfig.class)) return;
        YamlConfiguration config = event.getBukkitConfig();
        setEnabled(config.getBoolean(getConfigPath("enabled"), isEnabled()));
        setDisplayName(config.getString(getConfigPath("name"), getDisplayName()));
        setPrefix(config.getString(getConfigPath("prefix"), getPrefix()));
        event.scan(getClass(), this, this::getConfigPath);
    }

    @SneakyThrows
    @EventHandler
    default void on(YamlConfigSaveEvent event) {
        if (event.getYamlConfig() != SpringContext.getInstance().getBean(AbilitySlotsConfig.class)) return;
        event.set(getConfigPath("enabled"), isEnabled());
        event.set(getConfigPath("name"), getDisplayName());
        event.set(getConfigPath("prefix"), getPrefix());
        event.scan(getClass(), this, this::getConfigPath);
    }

    default String getConfigPath(String field) {
        return "categories." + getName() + "." + field;
    }
}
