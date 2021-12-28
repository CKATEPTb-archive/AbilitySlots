package ru.ckateptb.abilityslots.removalpolicy;

public class DurationRemovalPolicy implements RemovalPolicy {
    private final long exitTime;

    public DurationRemovalPolicy(long duration) {
        this.exitTime = System.currentTimeMillis() + duration;
    }

    @Override
    public boolean shouldRemove() {
        return System.currentTimeMillis() > exitTime;
    }
}