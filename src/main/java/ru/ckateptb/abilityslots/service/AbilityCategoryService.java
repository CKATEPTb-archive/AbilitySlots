package ru.ckateptb.abilityslots.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.category.AbilityCategory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class AbilityCategoryService {
    private final Map<String, AbilityCategory> categories = new HashMap<>();

    public void registerCategory(AbilityCategory abilityCategory) {
        categories.putIfAbsent(abilityCategory.getName().toLowerCase(), abilityCategory);
    }

    public Collection<AbilityCategory> getCategories() {
        return categories.values();
    }

    public AbilityCategory getCategory(String name) {
        return categories.get(name.toLowerCase());
    }
}
