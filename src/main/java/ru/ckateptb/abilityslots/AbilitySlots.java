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

package ru.ckateptb.abilityslots;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;
import ru.ckateptb.abilityslots.service.AbilityCategoryService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.service.AbilityUserService;
import ru.ckateptb.abilityslots.service.AddonService;
import ru.ckateptb.tablecloth.config.YamlConfigLoadEvent;
import ru.ckateptb.tablecloth.config.YamlConfigSaveEvent;
import ru.ckateptb.tablecloth.ioc.IoC;

//TODO Добавить собственные события
//TODO Пересмотреть код, может что улучшить, добавить, оптимизировать
//TODO Сделать Адон по мотивам Avatar (ProjectKorra), вынося необходимое в Tablecloth
public final class AbilitySlots extends JavaPlugin {
    @Getter
    private static AbilitySlots instance;
    private AbilitySlotsConfig abilitySlotsConfig;
    private AbilityUserService abilityUserService;
    private AbilityCategoryService categoryService;
    private AbilityService abilityService;
    private AddonService addonService;
    private PluginManager pluginManager;

    public AbilitySlots() {
        instance = this;
    }

    @Override
    public void onEnable() {
        this.pluginManager = Bukkit.getPluginManager();
        IoC.init(this);
        this.categoryService = IoC.get(AbilityCategoryService.class);
        this.abilityService = IoC.get(AbilityService.class);
        this.abilitySlotsConfig = IoC.get(AbilitySlotsConfig.class);
        this.addonService = IoC.get(AddonService.class);
        this.abilityUserService = IoC.get(AbilityUserService.class);
        this.reload();
    }

    public void reload() {
        YamlConfigLoadEvent.getHandlerList().unregister(this);
        YamlConfigSaveEvent.getHandlerList().unregister(this);
        this.pluginManager.callEvent(new AbilitySlotsReloadEvent());
        this.addonService.loadAddons();
        this.categoryService.getCategories().forEach(listener -> pluginManager.registerEvents(listener, this));
        this.abilityService.getAbilities().forEach(listener -> pluginManager.registerEvents(listener, this));
        this.abilitySlotsConfig.load();
        this.abilitySlotsConfig.save();
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.abilityUserService.getAbilityPlayer(player);
        }
    }
}
