package ru.ckateptb.abilityslots.service;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.event.AbilitySlotsReloadEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AbilityCategoryService implements Listener {
    private final Map<String, AbilityCategory> categories = new HashMap<>();

    public void registerCategory(AbilityCategory abilityCategory) {
        categories.putIfAbsent(abilityCategory.getName().toLowerCase(), abilityCategory);
    }

    public Collection<AbilityCategory> getCategories() {
        return categories.values();
    }

    public AbilityCategory getCategory(String name) {
        if(name == null) return null;
        return categories.get(name.toLowerCase());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void on(AbilitySlotsReloadEvent event) {
        this.categories.clear();
    }
}
