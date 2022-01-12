package ru.ckateptb.abilityslots.user;


import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface AbilityTargetEntity {
    Entity getEntity();

    default void setVelocity(Vector velocity) {
        getEntity().setVelocity(velocity);
    }
}
