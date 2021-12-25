package ru.ckateptb.abilityslots;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.service.AbilityCategoryService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.tablecloth.spring.plugin.SpringPlugin;

@Slf4j
//TODO Сделать собственную реализацию AbilityInstanceService (внутри AbilityService)
//TODO Сделать в Tablecloth API для Collision
//TODO Сделать команды для взаимодействия с плагином
//TODO Сделать Аннотацию Sequence которая будет регистрировать комбинации
//TODO Сделать Аннотацию Collision которая будет регистрировать коллизии
//TODO Сделать команду reload, которая будет перезагружать плагин
//TODO Добавить собственные события
//TODO Пересмотреть код, может что улучшить, добавить, оптимизировать
//TODO Сделать Адон по мотивам Avatar (ProjectKorra), вынося необходимое в Tablecloth
public final class AbilitySlots extends SpringPlugin {
    @Getter
    private static AbilitySlots instance;

    public AbilitySlots() {
        instance = this;
    }

    public static Logger getLog() {
        return log;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getContext().getBean(AbilityCategoryService.class).getCategories().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        getContext().getBean(AbilityService.class).getAbilities().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        AbilitySlotsConfig abilitySlotsConfig = getContext().getBean(AbilitySlotsConfig.class);
        abilitySlotsConfig.load();
        abilitySlotsConfig.save();
    }
}
