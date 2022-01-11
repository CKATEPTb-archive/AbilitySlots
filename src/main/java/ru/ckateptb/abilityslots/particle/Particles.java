package ru.ckateptb.abilityslots.particle;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import xyz.xenondevs.particle.ParticleEffect;
import xyz.xenondevs.particle.data.ParticleData;
import xyz.xenondevs.particle.data.color.ParticleColor;
import xyz.xenondevs.particle.data.texture.BlockTexture;
import xyz.xenondevs.particle.data.texture.ItemTexture;
import xyz.xenondevs.particle.data.texture.ParticleTexture;

import java.awt.*;
import java.util.Collection;
import java.util.function.Predicate;

/*
 * Regex for replace from {@link ParticleEffect}
 * Regex: '^    (.+)\(version ->.+\)'
 * Replace: '$1\(ParticleEffect.$1\)'
 */
public enum Particles {
    ASH(ParticleEffect.ASH),

    BARRIER(ParticleEffect.BARRIER),

    BLOCK_CRACK(ParticleEffect.BLOCK_CRACK),

    BLOCK_DUST(ParticleEffect.BLOCK_DUST),

    BUBBLE_COLUMN_UP(ParticleEffect.BUBBLE_COLUMN_UP),

    BLOCK_MARKER(ParticleEffect.BLOCK_MARKER),

    BUBBLE_POP(ParticleEffect.BUBBLE_POP),

    CAMPFIRE_COSY_SMOKE(ParticleEffect.CAMPFIRE_COSY_SMOKE),

    CAMPFIRE_SIGNAL_SMOKE(ParticleEffect.CAMPFIRE_SIGNAL_SMOKE),

    CLOUD(ParticleEffect.CLOUD),

    COMPOSTER(ParticleEffect.COMPOSTER),

    CRIMSON_SPORE(ParticleEffect.CRIMSON_SPORE),

    CRIT(ParticleEffect.CRIT),

    CRIT_MAGIC(ParticleEffect.CRIT_MAGIC),

    CURRENT_DOWN(ParticleEffect.CURRENT_DOWN),

    DAMAGE_INDICATOR(ParticleEffect.DAMAGE_INDICATOR),

    DOLPHIN(ParticleEffect.DOLPHIN),

    DRAGON_BREATH(ParticleEffect.DRAGON_BREATH),

    DRIP_LAVA(ParticleEffect.DRIP_LAVA),

    DRIP_WATER(ParticleEffect.DRIP_WATER),

    DRIPPING_DRIPSTONE_LAVA(ParticleEffect.DRIPPING_DRIPSTONE_LAVA),

    DRIPPING_DRIPSTONE_WATER(ParticleEffect.DRIPPING_DRIPSTONE_WATER),

    DRIPPING_HONEY(ParticleEffect.DRIPPING_HONEY),

    DRIPPING_OBSIDIAN_TEAR(ParticleEffect.DRIPPING_OBSIDIAN_TEAR),

    DUST_COLOR_TRANSITION(ParticleEffect.DUST_COLOR_TRANSITION),

    ELECTRIC_SPARK(ParticleEffect.ELECTRIC_SPARK),

    ENCHANTMENT_TABLE(ParticleEffect.ENCHANTMENT_TABLE),

    END_ROD(ParticleEffect.END_ROD),

    EXPLOSION_HUGE(ParticleEffect.EXPLOSION_HUGE),

    EXPLOSION_LARGE(ParticleEffect.EXPLOSION_LARGE),

    EXPLOSION_NORMAL(ParticleEffect.EXPLOSION_NORMAL),

    FALLING_DRIPSTONE_LAVA(ParticleEffect.FALLING_DRIPSTONE_LAVA),

    FALLING_DRIPSTONE_WATER(ParticleEffect.FALLING_DRIPSTONE_WATER),

    FALLING_DUST(ParticleEffect.FALLING_DUST),

    FALLING_HONEY(ParticleEffect.FALLING_HONEY),

    FALLING_NECTAR(ParticleEffect.FALLING_NECTAR),

    FALLING_OBSIDIAN_TEAR(ParticleEffect.FALLING_OBSIDIAN_TEAR),

    FALLING_SPORE_BLOSSOM(ParticleEffect.FALLING_SPORE_BLOSSOM),

    FIREWORKS_SPARK(ParticleEffect.FIREWORKS_SPARK),

    FLAME(ParticleEffect.FLAME),

    FLASH(ParticleEffect.FLASH),

    FOOTSTEP(ParticleEffect.FOOTSTEP),

    GLOW(ParticleEffect.GLOW),

    GLOW_SQUID_INK(ParticleEffect.GLOW_SQUID_INK),

    HEART(ParticleEffect.HEART),

    ITEM_CRACK(ParticleEffect.ITEM_CRACK),

    LANDING_HONEY(ParticleEffect.LANDING_HONEY),

    LANDING_OBSIDIAN_TEAR(ParticleEffect.LANDING_OBSIDIAN_TEAR),

    LAVA(ParticleEffect.LAVA),

    LIGHT(ParticleEffect.LIGHT),

    MOB_APPEARANCE(ParticleEffect.MOB_APPEARANCE),

    NAUTILUS(ParticleEffect.NAUTILUS),

    NOTE(ParticleEffect.NOTE),

    PORTAL(ParticleEffect.PORTAL),

    REDSTONE(ParticleEffect.REDSTONE),

    REVERSE_PORTAL(ParticleEffect.REVERSE_PORTAL),

    SCRAPE(ParticleEffect.SCRAPE),

    SLIME(ParticleEffect.SLIME),

    SMOKE_LARGE(ParticleEffect.SMOKE_LARGE),

    SMOKE_NORMAL(ParticleEffect.SMOKE_NORMAL),

    SNEEZE(ParticleEffect.SNEEZE),

    SNOWBALL(ParticleEffect.SNOWBALL),

    SNOWFLAKE(ParticleEffect.SNOWFLAKE),

    SNOW_SHOVEL(ParticleEffect.SNOW_SHOVEL),

    SOUL(ParticleEffect.SOUL),

    SOUL_FIRE_FLAME(ParticleEffect.SOUL_FIRE_FLAME),

    SPELL(ParticleEffect.SPELL),

    SPELL_INSTANT(ParticleEffect.SPELL_INSTANT),

    SPELL_MOB(ParticleEffect.SPELL_MOB),

    SPELL_MOB_AMBIENT(ParticleEffect.SPELL_MOB_AMBIENT),

    SPELL_WITCH(ParticleEffect.SPELL_WITCH),

    SPIT(ParticleEffect.SPIT),

    SPORE_BLOSSOM_AIR(ParticleEffect.SPORE_BLOSSOM_AIR),

    SQUID_INK(ParticleEffect.SQUID_INK),

    SUSPENDED(ParticleEffect.SUSPENDED),

    SUSPENDED_DEPTH(ParticleEffect.SUSPENDED_DEPTH),

    SWEEP_ATTACK(ParticleEffect.SWEEP_ATTACK),

    TOTEM(ParticleEffect.TOTEM),

    TOWN_AURA(ParticleEffect.TOWN_AURA),

    VIBRATION(ParticleEffect.VIBRATION),

    VILLAGER_ANGRY(ParticleEffect.VILLAGER_ANGRY),

    VILLAGER_HAPPY(ParticleEffect.VILLAGER_HAPPY),

    WARPED_SPORE(ParticleEffect.WARPED_SPORE),

    WATER_BUBBLE(ParticleEffect.WATER_BUBBLE),

    WATER_DROP(ParticleEffect.WATER_DROP),

    WATER_SPLASH(ParticleEffect.WATER_SPLASH),

    WATER_WAKE(ParticleEffect.WATER_WAKE),

    WAX_OFF(ParticleEffect.WAX_OFF),

    WAX_ON(ParticleEffect.WAX_ON),

    WHITE_ASH(ParticleEffect.WHITE_ASH);

    private final ParticleEffect effect;

    Particles(ParticleEffect effect) {
        this.effect = effect;
    }


    public void display(ParticleEffect effect, Location loc, int amount) {
        display(effect, loc, amount, 0, 0, 0);
    }

    public void display(ParticleEffect effect, Location loc, int amount, double offsetX, double offsetY, double offsetZ) {
        display(effect, loc, amount, offsetX, offsetY, offsetZ, 0);
    }

    public void display(ParticleEffect effect, Location loc, int amount, double offsetX, double offsetY, double offsetZ, double extra) {
        effect.display(loc, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, amount, null);
    }

    public void display(ParticleEffect effect, Location loc, int amount, double offsetX, double offsetY, double offsetZ, ItemStack itemStack) {
        display(effect, loc, amount, offsetX, offsetY, offsetZ, 0, new ItemTexture(itemStack));
    }

    public void display(ParticleEffect effect, Location loc, int amount, double offsetX, double offsetY, double offsetZ, Block block) {
        display(effect, loc, amount, offsetX, offsetY, offsetZ, 0, new BlockTexture(block.getType(), block.getData()));
    }

    public void display(ParticleEffect effect, Location loc, int amount, double offsetX, double offsetY, double offsetZ, BlockData data) {
        display(effect, loc, amount, offsetX, offsetY, offsetZ, 0, new BlockTexture(data.getMaterial()));
    }

    public void display(ParticleEffect effect, Location loc, int amount, double offsetX, double offsetY, double offsetZ, ParticleTexture data) {
        display(effect, loc, amount, offsetX, offsetY, offsetZ, 0, data);
    }

    public void display(ParticleEffect effect, Location loc, int amount, double offsetX, double offsetY, double offsetZ, double extra, ParticleTexture data) {
        effect.display(loc, (float) offsetX, (float) offsetY, (float) offsetZ, (float) extra, amount, data);
    }

    public void display(Location location, ParticleColor color, Player... players) {
        effect.display(location, color, players);
    }

    public void display(Location location, Color color, Player... players) {
        effect.display(location, color, players);
    }

    public void display(Location location, ParticleColor color, Predicate filter) {
        effect.display(location, color, filter);
    }

    public void display(Location location, Color color, Predicate filter) {
        effect.display(location, color, filter);
    }

    public void display(Location location, ParticleColor color, Collection<? extends Player> players) {
        effect.display(location, color, players);
    }

    public void display(Location location, Color color, Collection<? extends Player> players) {
        effect.display(location, color, players);
    }

    public void display(Location location, ParticleColor color) {
        effect.display(location, color);
    }

    public void display(Location location, Color color) {
        effect.display(location, color);
    }

    public void display(Location location, Player... players) {
        effect.display(location, players);
    }

    public void display(Location location, Predicate filter) {
        effect.display(location, filter);
    }

    public void display(Location location, Collection<? extends Player> players) {
        effect.display(location, players);
    }

    public void display(Location location) {
        effect.display(location);
    }

    public void display(Location location, Vector vector, float speed, int amount, ParticleData data, Player... players) {
        effect.display(location, vector, speed, amount, data, players);
    }

    public void display(Location location, Vector vector, float speed, int amount, ParticleData data, Predicate filter) {
        effect.display(location, vector, speed, amount, data, filter);
    }

    public void display(Location location, Vector vector, float speed, int amount, ParticleData data, Collection<? extends Player> players) {
        effect.display(location, vector, speed, amount, data, players);
    }

    public void display(Location location, Vector vector, float speed, int amount, ParticleData data) {
        effect.display(location, vector, speed, amount, data);
    }

    public void display(Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount, ParticleData data, Player... players) {
        effect.display(location, offsetX, offsetY, offsetZ, speed, amount, data, players);
    }

    public void display(Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount, ParticleData data, Predicate<Player> filter) {
        effect.display(location, offsetX, offsetY, offsetZ, speed, amount, data, filter);
    }

    public void display(Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount, ParticleData data) {
        effect.display(location, offsetX, offsetY, offsetZ, speed, amount, data);
    }

    public void display(Location location, float offsetX, float offsetY, float offsetZ, float speed, int amount, ParticleData data, Collection<? extends Player> players) {
        effect.display(location, offsetX, offsetY, offsetZ, speed, amount, data, players);
    }
}
