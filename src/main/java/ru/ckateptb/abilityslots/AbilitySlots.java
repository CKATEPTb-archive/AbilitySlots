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

//TODO Сделать более информативный способ активации способностей с массивом Enum в ответ
//TODO Сделать активацию пассивных способностей на все способы активации, если те указаны в способности (!И пересмотреть актуальность этой идеи, может она вовсе не нужна)
//TODO Реализовать информацию о способностях в BaseComponent
//TODO Сделать команду на просмотр доступных способностей
//TODO Сделать команду на просмотр привязанных способностей игрока
//TODO Сделать Аннотацию Sequence которая будет регистрировать комбинации
//TODO Сделать в Tablecloth API для Collision
//TODO Сделать Аннотацию Collision которая будет регистрировать коллизии
//TODO Сделать пресеты привязанных способностей
//TODO Добавить собственные события
//TODO Пересмотреть код, может что улучшить, добавить, оптимизировать
//TODO Сделать Адон по мотивам Avatar (ProjectKorra), вынося необходимое в Tablecloth
public final class AbilitySlots extends SpringPlugin {
    @Getter
    private static AbilitySlots instance;
    public AbilitySlotsConfig abilitySlotsConfig;
    public AbilityUserService abilityUserService;
    public AddonService addonService;
    public AbilitySlots() {
        instance = this;
    }

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
