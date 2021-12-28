package ru.ckateptb.abilityslots.removalpolicy;

import org.bukkit.Location;
import ru.ckateptb.abilityslots.user.AbilityUser;

import java.util.Objects;
import java.util.function.Supplier;

public class ProtectRemovalPolicy implements RemovalPolicy {
    private final AbilityUser user;
    private final Supplier<Location> locationSupplier;

    public ProtectRemovalPolicy(AbilityUser user, Supplier<Location> locationSupplier) {
        this.user = user;
        this.locationSupplier = locationSupplier;
    }

    @Override
    public boolean shouldRemove() {
        return !user.canUse(locationSupplier.get());
    }
}
