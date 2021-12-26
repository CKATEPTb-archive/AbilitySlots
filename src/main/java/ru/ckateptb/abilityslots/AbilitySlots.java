package ru.ckateptb.abilityslots;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;
import ru.ckateptb.abilityslots.service.AbilityCategoryService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.service.AbilityUserService;
import ru.ckateptb.abilityslots.service.AddonService;
import ru.ckateptb.tablecloth.spring.plugin.SpringPlugin;

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

    public AbilitySlotsConfig abilitySlotsConfig;
    public AbilityUserService abilityUserService;
    public AddonService addonService;

    @Override
    public void onEnable() {
        super.onEnable();
        getContext().getBean(AbilityCategoryService.class).getCategories().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        getContext().getBean(AbilityService.class).getAbilities().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        this.abilitySlotsConfig = getContext().getBean(AbilitySlotsConfig.class);
        this.addonService = getContext().getBean(AddonService.class);
        this.abilityUserService = getContext().getBean(AbilityUserService.class);
        this.reload();
    }

    public void reload() {
        Bukkit.getPluginManager().callEvent(new AbilitySlotsReloadEvent());
        this.addonService.loadAddons();
        this.abilitySlotsConfig.load();
        this.abilitySlotsConfig.save();
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.abilityUserService.getAbilityPlayer(player);
        }
    }
}
