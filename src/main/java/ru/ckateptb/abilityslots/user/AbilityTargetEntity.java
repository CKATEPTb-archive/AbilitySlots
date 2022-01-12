package ru.ckateptb.abilityslots.user;


import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import ru.ckateptb.tablecloth.math.ImmutableVector;

public interface AbilityTargetEntity {
    Entity getEntity();

    default void setVelocity(Vector velocity) {
        getEntity().setVelocity(velocity);
    }

    default ImmutableVector getLocation() {
        return new ImmutableVector(getEntity().getLocation());
    }

    default ImmutableVector getCenterLocation() {
        return getLocation().add(new ImmutableVector(0, getEntity().getHeight() / 2, 0));
    }

    default double getDistanceAboveGround() {
        return getDistanceAboveGround(false);
    }

    default double getDistanceAboveGround(boolean ignoreLiquids) {
        return getLocation().getDistanceAboveGround(getWorld(), ignoreLiquids);
    }

    default World getWorld() {
        return getEntity().getWorld();
    }
}
