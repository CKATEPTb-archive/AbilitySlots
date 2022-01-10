package ru.ckateptb.abilityslots.ability.info;

import ru.ckateptb.abilityslots.ability.Ability;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CollisionParticipant {
    Class<? extends Ability>[] destroyAbilities() default {};
}