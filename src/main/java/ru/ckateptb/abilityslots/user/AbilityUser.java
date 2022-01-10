package ru.ckateptb.abilityslots.user;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.BlockIterator;
import org.checkerframework.checker.nullness.qual.Nullable;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.conditional.CompositeAbilityConditional;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.service.AbilityInstanceService;
import ru.ckateptb.abilityslots.slot.AbilitySlotContainer;
import ru.ckateptb.tablecloth.collision.RayTrace;
import ru.ckateptb.tablecloth.math.FastMath;
import ru.ckateptb.tablecloth.math.Vector3d;
import ru.ckateptb.tablecloth.spring.SpringContext;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface AbilityUser {
    LivingEntity getEntity();

    AbilitySlotContainer getSlotContainer();

    void setSlotContainer(AbilitySlotContainer slotContainer);

    default AbilityInformation[] getAbilities() {
        return getSlotContainer().getAbilities();
    }

    default AbilityInformation getAbility(int slot) {
        return getSlotContainer().getAbility(slot);
    }

    AbilityInformation getSelectedAbility();

    void clearAbilities();

    default void setAbility(int slot, AbilityInformation ability) {
        getSlotContainer().setAbility(slot, ability);
    }

    void setCooldown(AbilityInformation ability, long duration);

    boolean hasCooldown(AbilityInformation ability);

    Map<AbilityInformation, Long> getCooldowns();

    boolean canActivate(AbilityInformation ability);

    CompositeAbilityConditional getAbilityActivateConditional();

    void setAbilityActivateConditional(CompositeAbilityConditional conditional);

    boolean canUse(Location location);


    /**
     * Attempt to find a possible block source that matches the given predicate.
     *
     * @param range         the max range to check
     * @return the source Vector3d
     */
    default @Nullable Vector3d findPosition(double range) {
        return findPosition(range, true);
    }

    /**
     * Attempt to find a possible block source that matches the given predicate.
     *
     * @param range         the max range to check
     * @param ignoreLiquids – true to scan through liquids
     * @return the source Vector3d
     */
    default @Nullable Vector3d findPosition(double range, boolean ignoreLiquids) {
        return findPosition(range, entity -> true, ignoreLiquids);
    }

    /**
     * Attempt to find a possible block source that matches the given predicate.
     *
     * @param range         the max range to check
     * @param predicate     the predicate to check
     * @param ignoreLiquids – true to scan through liquids
     * @return the source Vector3d
     */
    default @Nullable Vector3d findPosition(double range, Predicate<Entity> predicate, boolean ignoreLiquids) {
        return RayTrace.of(getEntity()).range(range).filter(predicate).ignoreLiquids(ignoreLiquids).result(getEntity().getWorld()).position();
    }

    /**
     * Attempt to find a possible block source that matches the given predicate.
     *
     * @param range     the max range to check
     * @param predicate the predicate to check
     * @return the source block if one was found, null otherwise
     */
    default @Nullable Block findBlock(double range, Predicate<Block> predicate) {
        BlockIterator it = new BlockIterator(getEntity(), Math.min(100, FastMath.ceil(range)));
        while (it.hasNext()) {
            Block block = it.next();
            if (block.getType().isAir()) {
                continue;
            }
            if (predicate.test(block)) {
                return block;
            }
            if (!block.isPassable()) {
                break;
            }
        }
        return null;
    }

    /**
     * Attempt to find a possible block source that matches the given predicate.
     *
     * @param range     the max range to check
     * @param predicate the predicate to check
     * @return the source LivingEntity if one was found, null otherwise
     */
    default @Nullable LivingEntity findLivingEntity(double range, Predicate<LivingEntity> predicate) {
        LivingEntity entity = getEntity();
        return (LivingEntity) RayTrace.of(entity)
                .range(range)
                .type(RayTrace.Type.ENTITY)
                .filter(e -> e instanceof LivingEntity target && target != entity && predicate.test(target))
                .result(entity.getWorld()).entity();
    }

    /**
     * Attempt to find a possible block source that matches the given predicate.
     *
     * @param range        the max range to check
     * @param ignoreBlocks – true to scan through blocks
     * @return the source Entity if one was found, null otherwise
     */
    default @Nullable Entity getTargetEntity(int range, boolean ignoreBlocks) {
        LivingEntity entity = getEntity();
        return entity.getTargetEntity(range, ignoreBlocks);
    }

    default Vector3d getLocation() {
        return new Vector3d(getEntity().getLocation());
    }

    default Vector3d getEyeLocation() {
        return new Vector3d(getEntity().getEyeLocation());
    }

    default Vector3d getDirection() {
        return new Vector3d(getEntity().getEyeLocation().getDirection());
    }

    default World getWorld() {
        return getEntity().getWorld();
    }

    default Collection<? extends Ability> getActiveAbilities() {
        return SpringContext.getInstance().getBean(AbilityInstanceService.class).getAbilityUserInstances(this);
    }

    default Collection<? extends Ability> getActiveAbilities(Class<? extends Ability> type) {
        return SpringContext.getInstance().getBean(AbilityInstanceService.class).getAbilityUserInstances(this, type);
    }

    default Collection<? extends Ability> getPassives() {
        return getActiveAbilities().stream().filter(ability -> ability.getInformation().isActivatedBy(ActivationMethod.PASSIVE)).collect(Collectors.toList());
    }

    default boolean destroyAbility(Class<? extends Ability> type) {
        return SpringContext.getInstance().getBean(AbilityInstanceService.class).destroyInstanceType(this, type);
    }

    default void destroyAbility(Ability ability) {
        SpringContext.getInstance().getBean(AbilityInstanceService.class).destroyInstance(this, ability);
    }

    default void destroyAbilities() {
        SpringContext.getInstance().getBean(AbilityInstanceService.class).destroyAbilityUserInstances(this);
    }

    default void setCooldown(Ability ability) {
        setCooldown(ability.getInformation());
    }

    default void setCooldown(AbilityInformation information) {
        setCooldown(information, information.getCooldown());
    }
}
