package ru.ckateptb.abilityslots;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;
import ru.ckateptb.abilityslots.service.AbilityCategoryService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.service.AbilityUserService;
import ru.ckateptb.abilityslots.service.AddonService;
import ru.ckateptb.tablecloth.config.YamlConfigLoadEvent;
import ru.ckateptb.tablecloth.config.YamlConfigSaveEvent;
import ru.ckateptb.tablecloth.spring.plugin.SpringPlugin;

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
        super.onEnable();
        this.pluginManager = Bukkit.getPluginManager();
        this.categoryService = getContext().getBean(AbilityCategoryService.class);
        this.abilityService = getContext().getBean(AbilityService.class);
        this.abilitySlotsConfig = getContext().getBean(AbilitySlotsConfig.class);
        this.addonService = getContext().getBean(AddonService.class);
        this.abilityUserService = getContext().getBean(AbilityUserService.class);
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
