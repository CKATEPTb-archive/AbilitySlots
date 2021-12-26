package ru.ckateptb.abilityslots.ability.info;

import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AbilityInfo {
    String name();

    String displayName();

    String category();

    String description();

    String instruction();

    long cooldown() default 0;

    int cost() default 0;

    String author();

    ActivationMethod[] activationMethods();

    boolean canBindToSlot() default true;
}
