package ru.ckateptb.abilityslots.ability.info;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.category.AbilityCategory;

import java.util.Arrays;

@Getter
@Setter
public class AnnotationBasedAbilityInformation implements AbilityInformation {
    private final String name;
    private final Class<? extends Ability> abilityClass;
    private final AbilityCategory category;
    private final String author;
    private final ActivationMethod[] activationMethods;
    private long cooldown;
    private int cost;
    private boolean enabled;
    private String displayName;
    private String description;
    private String instruction;

    public AnnotationBasedAbilityInformation(AbilityInfo abilityInfo, AbilityCategory abilityCategory, Class<? extends Ability> abilityClass) {
        this.name = abilityInfo.name();
        this.abilityClass = abilityClass;
        this.category = abilityCategory;
        this.displayName = abilityInfo.displayName();
        this.description = abilityInfo.description();
        this.instruction = abilityInfo.instruction();
        this.cooldown = abilityInfo.cooldown();
        this.cost = abilityInfo.cost();
        this.author = abilityInfo.author();
        this.activationMethods = abilityInfo.activationMethods();
    }

    @Override
    public boolean isActivatedBy(ActivationMethod method) {
        return Arrays.asList(this.activationMethods).contains(method);
    }

    @Override
    public String getFormattedName() {
        return this.category.getPrefix() + this.displayName;
    }

    @Override
    @SneakyThrows
    public Ability createAbility() {
        return abilityClass.getConstructor().newInstance();
    }
}
