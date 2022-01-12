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
