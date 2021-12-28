package ru.ckateptb.abilityslots.removalpolicy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompositeRemovalPolicy implements RemovalPolicy {
    private final List<RemovalPolicy> policies;

    public CompositeRemovalPolicy(List<RemovalPolicy> policies) {
        this.policies = policies;
    }

    public CompositeRemovalPolicy(RemovalPolicy... policies) {
        // Create as an ArrayList instead of fixed-sized so policies can be added/removed.
        this.policies = new ArrayList<>(Arrays.asList(policies));
    }

    @Override
    public boolean shouldRemove() {
        if (policies.isEmpty()) return false;

        for (RemovalPolicy policy : policies) {
            if (policy.shouldRemove()) {
                return true;
            }
        }
        return false;
    }

    public void addPolicy(RemovalPolicy... policies) {
        this.policies.addAll(Arrays.asList(policies));
    }

    public void removePolicyType(Class<? extends RemovalPolicy> type) {
        policies.removeIf((policy) -> type.isAssignableFrom(policy.getClass()));
    }
}
