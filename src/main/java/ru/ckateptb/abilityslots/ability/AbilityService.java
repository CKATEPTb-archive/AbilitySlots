package ru.ckateptb.abilityslots.ability;

import org.springframework.stereotype.Service;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.category.CategoryService;

import java.util.ArrayList;
import java.util.List;

@Service
public class AbilityService {
    private final CategoryService categoryService;

    public AbilityService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<AbilityInformation> getAbilities() {
        List<AbilityInformation> abilities = new ArrayList<>();
        categoryService.getCategories().forEach(category -> abilities.addAll(category.getAbilities()));
        return abilities;
    }
}
