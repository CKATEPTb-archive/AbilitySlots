package ru.ckateptb.abilityslots.removalpolicy;

import org.bukkit.Location;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.Objects;
import java.util.function.Supplier;

public class OutOfRangeRemovalPolicy implements RemovalPolicy {
    private final Supplier<Location> fromSupplier;
    private final AbilityUser user;
    private final double range;

    public OutOfRangeRemovalPolicy(AbilityUser user, double range, Supplier<Location> from) {
        this.user = user;
        this.range = range;
        this.fromSupplier = from;
    }

    @Override
    public boolean shouldRemove() {
        if (this.range == 0) return false;

        Location from = this.fromSupplier.get();

        if (!Objects.equals(from.getWorld(), user.getEntity().getWorld())) return true;

        return from.distance(this.user.getEntity().getLocation()) >= this.range;
    }
}
