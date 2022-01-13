/*
 * Copyright (c) 2022 CKATEPTb <https://github.com/CKATEPTb>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.ckateptb.abilityslots.entity;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.tablecloth.collision.collider.RayCollider;
import ru.ckateptb.tablecloth.math.ImmutableVector;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * {@link LivingEntity} wrapper
 */
public interface AbilityTarget extends AbilityTargetEntity {

    /**
     * Wraps the specified {@link Entity} in {@link AbilityTargetEntity}.
     *
     * @param entity {@link Entity} that should to wrap
     * @return {@link AbilityTargetEntity} of specified {@link Entity}
     */
    static AbilityTargetEntity of(Entity entity) {
        return () -> entity;
    }

    /**
     * Wraps the specified {@link LivingEntity} in {@link AbilityTarget}.
     *
     * @param entity {@link LivingEntity} that should to wrap
     * @return {@link AbilityTarget} of specified {@link LivingEntity}
     */
    static AbilityTarget of(LivingEntity entity) {
        return () -> entity;
    }

    /**
     * @return wrapped {@link LivingEntity}
     */
    LivingEntity getEntity();

    /**
     * Deals the given amount of damage to this entity, from a specified entity.
     *
     * @param amount  Amount of damage to deal
     * @param ability {@link Ability} that caused this damage
     */
    default void damage(double amount, Ability ability) {
        damage(amount, ability, false);
    }

    /**
     * Deals the given amount of damage to this entity, from a specified entity.
     *
     * @param amount              Amount of damage to deal
     * @param ability             {@link Ability} that caused this damage
     * @param ignoreNoDamageTicks cause this damage ignoring noDamageTicks
     */
    default void damage(double amount, Ability ability, boolean ignoreNoDamageTicks) {
        LivingEntity entity = getEntity();
        if (ignoreNoDamageTicks) {
            entity.setNoDamageTicks(0);
        }
        entity.damage(amount, ability.getUser().getEntity());
    }

    /**
     * @return true if entity isn't player or player is in sneak mode,
     */
    default boolean isSneaking() {
        return !(getEntity() instanceof Player player) || player.isSneaking();
    }


    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     * <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * <p>blockFilter <b>block -> true</b> block predicate</p>
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range) {
        return findPosition(range, block -> true);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * @param ignoreLiquids should ignore liquids on scan path
     * <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * <p>blockFilter <b>block -> true</b> block predicate</p>
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, boolean ignoreLiquids) {
        return findPosition(range, ignoreLiquids, block -> true);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * @param ignoreLiquids should ignore liquids on scan path
     * <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, boolean ignoreLiquids, Predicate<Block> blockFilter) {
        return findPosition(range, ignoreLiquids, entity -> true, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     * <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, Predicate<Block> blockFilter) {
        return findPosition(range, entity -> true, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, false, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, true, ignoreLiquids, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, false, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * @param ignoreBlocks should ignore blocks on scan path
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, false, ignoreBlocks, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     * <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * <p>blockFilter <b>block -> true</b> block predicate</p>
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, double raySize) {
        return findPosition(range, raySize, block -> true);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     * <p>entityFilter <b>entity -> true</b> entity predicate</p>
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, double raySize, Predicate<Block> blockFilter) {
        return findPosition(range, raySize, entity -> true, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * <p>ignoreLiquids <b>default false</b> should ignore liquids on scan path</p>
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, double raySize, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, raySize, false, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on scan path</p>
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, double raySize, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, raySize, true, ignoreLiquids, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * <p>ignoreBlocks <b>default false</b> should ignore blocks on scan path</p>
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, double raySize, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, raySize, false, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value
     * <p>ignoreEntity <b>default false</b> should ignore entity on scan path</p>
     * @param ignoreBlocks should ignore blocks on scan path
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, double raySize, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, raySize, false, ignoreBlocks, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value</p>
     * @param ignoreEntity should ignore entity on scan path
     * @param ignoreBlocks should ignore blocks on scan path
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, boolean ignoreEntity, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        return findPosition(range, 0, ignoreEntity, ignoreBlocks, ignorePassable, ignoreLiquids, entityFilter, blockFilter);
    }

    /**
     * Get distant position found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value
     * @param ignoreEntity should ignore entity on scan path
     * @param ignoreBlocks should ignore blocks on scan path
     * @param ignorePassable should ignore passable blocks on scan path
     * @param ignoreLiquids should ignore liquids on scan path
     * @param entityFilter entity predicate
     * @param blockFilter block predicate
     * @return distant position in the direction of the entity
     */
    default ImmutableVector findPosition(double range, double raySize, boolean ignoreEntity, boolean ignoreBlocks, boolean ignorePassable, boolean ignoreLiquids, Predicate<Entity> entityFilter, Predicate<Block> blockFilter) {
        LivingEntity source = getEntity();
        return new ImmutableVector(new RayCollider(source, range, raySize).getPosition(ignoreEntity, ignoreBlocks, ignoreLiquids, ignorePassable, entityFilter.and(entity -> entity != source), blockFilter.and(block -> !block.getType().isAir())).orElse(getDirection().normalize().multiply(range)));
    }

    /**
     * Get the first block found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * <p>ignoreLiquid <b>default false</b> should ignore liquids on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on the scan path</p>
     * <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    default @Nullable Block findBlock(double range) {
        return findBlock(range, 0);
    }

    /**
     * Get the first block found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * <p>ignoreLiquid <b>default false</b> should ignore liquids on scan path</p>
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on the scan path</p>
     * <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    default @Nullable Block findBlock(double range, double raySize) {
        return findBlock(range, raySize, false);
    }

    /**
     * Get the first block found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * @param ignoreLiquid should ignore liquids on scan path
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on the scan path</p>
     * <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    default @Nullable Block findBlock(double range, double raySize, boolean ignoreLiquid) {
        return findBlock(range, raySize, ignoreLiquid, true);
    }

    /**
     * Get the first block found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * @param ignoreLiquid should ignore liquids on scan path
     * @param ignorePassable should ignore passable blocks on the scan path
     * <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    default @Nullable Block findBlock(double range, double raySize, boolean ignoreLiquid, boolean ignorePassable) {
        return findBlock(range, raySize, ignoreLiquid, ignorePassable, block -> true);
    }

    /**
     * Get the first block found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * @param ignoreLiquid should ignore liquids on scan path
     * <p>ignorePassable <b>default true</b> should ignore passable blocks on the scan path</p>
     * <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    default @Nullable Block findBlock(double range, boolean ignoreLiquid) {
        return findBlock(range, ignoreLiquid, true);
    }

    /**
     * Get the first block found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * @param ignoreLiquid should ignore liquids on scan path
     * @param ignorePassable should ignore passable blocks on the scan path
     * <p>filter <b>default block -> true</b> block predicate</p>
     * @return Block that was found in the direction of the entity or null
     */
    default @Nullable Block findBlock(double range, boolean ignoreLiquid, boolean ignorePassable) {
        return findBlock(range, ignoreLiquid, ignorePassable, block -> true);
    }

    /**
     * Get the first block found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * @param ignoreLiquid should ignore liquids on scan path
     * @param ignorePassable should ignore passable blocks on the scan path
     * @param filter block predicate
     * @return Block that was found in the direction of the entity or null
     */
    default @Nullable Block findBlock(double range, boolean ignoreLiquid, boolean ignorePassable, Predicate<Block> filter) {
        return findBlock(range, 0, ignoreLiquid, ignorePassable, filter);
    }

    /**
     * Get the first block found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * @param ignoreLiquid should ignore liquids on scan path
     * @param ignorePassable should ignore passable blocks on the scan path
     * @param filter block predicate
     * @return Block that was found in the direction of the entity or null
     */
    default @Nullable Block findBlock(double range, double raySize, boolean ignoreLiquid, boolean ignorePassable, Predicate<Block> filter) {
        return new RayCollider(getEntity(), range, raySize).getBlock(ignoreLiquid, ignorePassable, filter.and(block -> !block.getType().isAir())).orElse(null);
    }

    /**
     * Get the first LivingEntity found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * <p>ignoreBlock <b>default false</b> - should ignore blocks on scan path</p>
     * <p>predicate <b>default true</b> - filter for founded entity</p>
     * @return LivingEntity that was found in the direction of the entity or null
     */
    default @Nullable LivingEntity findLivingEntity(double range, double raySize) {
        return findLivingEntity(range, raySize, false, entity -> true);
    }

    /**
     * Get the first LivingEntity found in the entity direction
     * @param range this is the maximum distance to scan
     * <p>raySize <b>default 0</b> - entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * <p>ignoreBlock <b>default false</b> - should ignore blocks on scan path</p>
     * <p>predicate <b>default true</b> - filter for founded entity</p>
     * @return LivingEntity that was found in the direction of the entity or null
     */
    default @Nullable LivingEntity findLivingEntity(double range) {
        return findLivingEntity(range, entity -> true);
    }

    /**
     * Get the first LivingEntity found in the entity direction
     * @param range this is the maximum distance to scan
     * @param predicate filter for founded entity
     * <p>raySize <b>default 0</b> - entity bounding boxes will be uniformly expanded (or shrinked) by this value before</p>
     * <p>ignoreBlock <b>default false</b> - should ignore blocks on scan path</p>
     * @return LivingEntity that was found in the direction of the entity or null
     */
    default @Nullable LivingEntity findLivingEntity(double range, Predicate<LivingEntity> predicate) {
        return findLivingEntity(range, 0, false, predicate);
    }

    /**
     * Get the first LivingEntity found in the entity direction
     * @param range this is the maximum distance to scan
     * @param raySize entity bounding boxes will be uniformly expanded (or shrinked) by this value before
     * @param ignoreBlock should ignore blocks on scan path
     * @param predicate filter for founded entity
     * @return LivingEntity that was found in the direction of the entity or null
     */
    default @Nullable LivingEntity findLivingEntity(double range, double raySize, boolean ignoreBlock, Predicate<LivingEntity> predicate) {
        LivingEntity livingEntity = getEntity();
        if (!ignoreBlock) {
            Block block = findBlock(range, raySize, true);
            if (block != null) {
                range = new ImmutableVector(block.getLocation().toCenterLocation()).distance(getCenterLocation());
            }
        }
        return (LivingEntity) new RayCollider(livingEntity, range, raySize).getEntity(entity -> entity instanceof LivingEntity target && target != livingEntity && predicate.test(target)).orElse(null);
    }

    /**
     * Gets information about the entity being targeted
     *
     * @param range  this is the maximum distance to scan
     * @param ignoreBlocks true to scan through blocks
     * @return entity being targeted, or null if no entity is targeted
     */
    default @Nullable Entity getTargetEntity(int range, boolean ignoreBlocks) {
        LivingEntity entity = getEntity();
        return entity.getTargetEntity(range, ignoreBlocks);
    }

    /**
     * Gets {@link ImmutableVector} of {@link LivingEntity#getEyeLocation()}
     *
     * @return an {@link ImmutableVector} of entity eye location
     */
    default ImmutableVector getEyeLocation() {
        return new ImmutableVector(getEntity().getEyeLocation());
    }

    /**
     * Gets {@link ImmutableVector} of {@link LivingEntity} direction
     *
     * @return an {@link ImmutableVector} pointing the direction of entity eye location's pitch and yaw
     */
    default ImmutableVector getDirection() {
        return new ImmutableVector(getEntity().getEyeLocation().getDirection());
    }

    /**
     * Adds the given {@link PotionEffect} to the living entity.
     *
     * @param ability this method is called from
     * @param effect PotionEffect to be added
     * @return whether the effect could be added
     */
    default void addPotionEffect(Ability ability, @NotNull PotionEffect effect) {
        ability.sync(() -> getEntity().addPotionEffect(effect));
    }

    /**
     * Adds the given {@link PotionEffect} to the living entity.
     * <p>
     * Only one potion effect can be present for a given {@link
     * PotionEffectType}.
     *
     * @param ability this method is called from
     * @param effect PotionEffect to be added
     * @param force whether conflicting effects should be removed
     * @return whether the effect could be added
     * @deprecated no need to force since multiple effects of the same type are
     * now supported.
     */
    @Deprecated
    default void addPotionEffect(Ability ability, @NotNull PotionEffect effect, boolean force) {
        ability.sync(() -> getEntity().addPotionEffect(effect, force));
    }

    /**
     * Attempts to add all of the given {@link PotionEffect} to the living
     * entity.
     *
     * @param ability this method is called from
     * @param effects the effects to add
     * @return whether all of the effects could be added
     */
    default void addPotionEffects(Ability ability, @NotNull Collection<PotionEffect> effects) {
        ability.sync(() -> getEntity().addPotionEffects(effects));
    }

    /**
     * Returns whether the living entity already has an existing effect of
     * the given {@link PotionEffectType} applied to it.
     *
     * @param ability this method is called from
     * @param type the potion type to check
     * @return whether the living entity has this potion effect active on them
     */
    default boolean hasPotionEffect(Ability ability, @NotNull PotionEffectType type) {
        return getEntity().hasPotionEffect(type);
    }

    /**
     * Returns the active {@link PotionEffect} of the specified type.
     * <p>
     * If the effect is not present on the entity then null will be returned.
     *
     * @param ability this method is called from
     * @param type the potion type to check
     * @return the effect active on this entity, or null if not active.
     */
    @org.jetbrains.annotations.Nullable
    default PotionEffect getPotionEffect(Ability ability, @NotNull PotionEffectType type) {
        return getEntity().getPotionEffect(type);
    }

    /**
     * Removes any effects present of the given {@link PotionEffectType}.
     *
     * @param ability this method is called from
     * @param type the potion type to remove
     */
    default void removePotionEffect(Ability ability, @NotNull PotionEffectType type) {
        ability.sync(() -> getEntity().removePotionEffect(type));
    }

    /**
     * Returns all currently active {@link PotionEffect}s on the living
     * entity.
     *
     * @return a collection of {@link PotionEffect}s
     */
    @NotNull
    default Collection<PotionEffect> getActivePotionEffects() {
        return getEntity().getActivePotionEffects();
    }

    /**
     * Returns true if this entity has been marked for removal.
     *
     * @return True if it is dead.
     */
    default boolean isDead() {
        return getEntity().isDead();
    }

    /**
     * @return true.
     */
    default boolean isSprinting() {
        return true;
    }
}
