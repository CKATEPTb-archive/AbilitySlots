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
