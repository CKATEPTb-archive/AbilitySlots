package ru.ckateptb.abilityslots.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class CategoryService {
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
