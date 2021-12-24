package ru.ckateptb.abilityslots;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import ru.ckateptb.abilityslots.ability.AbilityService;
import ru.ckateptb.abilityslots.category.CategoryService;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.tablecloth.spring.plugin.SpringPlugin;

@Slf4j
public final class AbilitySlots extends SpringPlugin {
    public static Logger getLog() {
        return log;
    }

    @Getter
    private static AbilitySlots instance;

    public AbilitySlots() {
        instance = this;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getContext().getBean(CategoryService.class).getCategories().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        getContext().getBean(AbilityService.class).getAbilities().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        AbilitySlotsConfig abilitySlotsConfig = getContext().getBean(AbilitySlotsConfig.class);
        abilitySlotsConfig.load();
        abilitySlotsConfig.save();
    }
}