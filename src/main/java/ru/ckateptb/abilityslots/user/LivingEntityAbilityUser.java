package ru.ckateptb.abilityslots.user;

import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import ru.ckateptb.abilityslots.ability.conditional.*;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.board.AbilityBoardHolder;
import ru.ckateptb.abilityslots.energy.EnergyHolder;
import ru.ckateptb.abilityslots.slot.AbilitySlotContainer;
import ru.ckateptb.abilityslots.slot.DefaultAbilitySlotContainer;
import ru.ckateptb.abilityslots.slot.EmptyAbilitySlotContainer;

import java.util.HashMap;
import java.util.Map;

public class LivingEntityAbilityUser implements AbilityUser, AbilityBoardHolder, EnergyHolder {
    private final Map<AbilityInformation, Long> cooldowns = new HashMap<>();
    private CompositeAbilityActivateConditional abilityActivateConditional = new CompositeAbilityActivateConditional();

    protected final LivingEntity livingEntity;
    protected AbilitySlotContainer slotContainer;

    public LivingEntityAbilityUser(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
        this.slotContainer = new DefaultAbilitySlotContainer();
        this.abilityActivateConditional.add(
                new CategoryAbilityActivateConditional(),
                new CooldownAbilityActivateConditional(),
                new EnabledAbilityActivateConditional(),
                new GameModeAbilityActivateConditional(GameMode.SPECTATOR),
                new PermissionAbilityActivateConditional()
        );
    }

    @Override
    public LivingEntity getEntity() {
        return livingEntity;
    }

    @Override
    public AbilitySlotContainer getSlotContainer() {
        return slotContainer;
    }

    @Override
    public void setSlotContainer(AbilitySlotContainer slotContainer) {
        this.slotContainer = slotContainer;
    }

    @Override
    public void clearAbilities() {
        for (int i = 1; i <= 9; i++) {
            setAbility(i, null);
        }
    }

    @Override
    public void setCooldown(AbilityInformation ability, long duration) {
        long current = cooldowns.getOrDefault(ability, 0L);

        // Only set cooldown if the new one is larger.
        if (duration > 0 && duration > current - System.currentTimeMillis()) {
            cooldowns.put(ability, System.currentTimeMillis() + duration);
        }
    }

    @Override
    public boolean hasCooldown(AbilityInformation ability) {
        if (!cooldowns.containsKey(ability)) {
            return false;
        }

        long time = System.currentTimeMillis();
        long end = cooldowns.get(ability);

        return time < end;
    }

    @Override
    public Map<AbilityInformation, Long> getCooldowns() {
        return cooldowns;
    }

    @Override
    public boolean canActivate(AbilityInformation ability) {
        return abilityActivateConditional.canActivate(this, ability);
    }

    @Override
    public CompositeAbilityActivateConditional getAbilityActivateConditional() {
        return abilityActivateConditional;
    }

    @Override
    public void setAbilityActivateConditional(CompositeAbilityActivateConditional conditional) {
        this.abilityActivateConditional = conditional;
    }

    @Override
    public void updateAbilityBoard() {

    }

    @Override
    public void enableAbilityBoard() {

    }

    @Override
    public void disableAbilityBoard() {

    }

    @Override
    public boolean isAbilityBoardEnabled() {
        return false;
    }

    @Override
    public double getEnergy() {
        return Double.MAX_VALUE;
    }

    @Override
    public void setEnergy(double value) {

    }

    @Override
    public double getMaxEnergy() {
        return Double.MAX_VALUE;
    }

    @Override
    public void updateEnergyBar() {

    }

    @Override
    public void enableEnergyBar() {

    }

    @Override
    public void disableEnergyBar() {

    }

    @Override
    public boolean isEnergyBarEnabled() {
        return false;
    }
}
