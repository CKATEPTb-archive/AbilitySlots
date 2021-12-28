package ru.ckateptb.abilityslots.removalpolicy;

import org.bukkit.Location;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.Objects;
import java.util.function.Supplier;

public class OutOfRangeRemovalPolicy implements RemovalPolicy {
    private final Supplier<Location> fromSupplier;
    private final Supplier<Location> toSupplier;
    private final double range;

    public OutOfRangeRemovalPolicy(Supplier<Location> fromSupplier, Supplier<Location> toSupplier, double range) {
        this.fromSupplier = fromSupplier;
        this.toSupplier = toSupplier;
        this.range = range;
    }

    @Override
    public boolean shouldRemove() {
        if (this.range == 0) return false;

        Location from = this.fromSupplier.get();
        Location to = this.toSupplier.get();

        if (!Objects.equals(from.getWorld(), to.getWorld())) return true;
        return from.distance(to) >= this.range;
    }
}
