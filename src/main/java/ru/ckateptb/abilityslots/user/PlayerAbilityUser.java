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

package ru.ckateptb.abilityslots.user;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.board.AbilityBoard;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.predicate.AbilityConditional;
import ru.ckateptb.abilityslots.config.AbilityCastPreventType;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.energy.EnergyBar;
import ru.ckateptb.abilityslots.entity.AbilityTargetPlayer;
import ru.ckateptb.abilityslots.predicate.CategoryConditional;
import ru.ckateptb.abilityslots.service.AbilityInstanceService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.slot.AbilitySlotContainer;
import ru.ckateptb.abilityslots.slot.DefaultAbilitySlotContainer;
import ru.ckateptb.abilityslots.storage.AbilitySlotsStorage;
import ru.ckateptb.abilityslots.storage.PlayerAbilityTable;
import ru.ckateptb.abilityslots.storage.PresetAbilityTable;
import ru.ckateptb.tablecloth.async.AsyncService;
import ru.ckateptb.tablecloth.storage.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerAbilityUser extends LivingEntityAbilityUser implements AbilityTargetPlayer {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final AbilityBoard abilityBoard;
    @Getter
    private final EnergyBar energyBar;
    private final AbilityConditional abilityBindConditional;
    private final AbilityConditional abilityUseConditional;
    private final CategoryConditional categoryUseConditional = CategoryConditional.HAS_CATEGORY_PERMISSION;
    private final Dao<PlayerAbilityTable, String> abilityStorage;
    private final Dao<PresetAbilityTable, String> presetStorage;
    private final HashMap<String, String> presets = new HashMap<>();
    private final String uuid;
    private final AsyncService asyncService;
    private boolean presetsIsLocked = false;
    private double energy = 0;

    public PlayerAbilityUser(Player livingEntity, AbilitySlotsConfig config, AbilityService abilityService, AbilityInstanceService abilityInstanceService, AbilitySlotsStorage storage, AsyncService asyncService) {
        super(livingEntity, config);
        this.abilityBoard = new AbilityBoard(this, config, abilityService);
        this.energyBar = new EnergyBar(this, config);
        this.abilityBindConditional = new AbilityConditional.Builder()
                .hasPermission()
                .hasCategory()
                .isEnabled()
                .isBindable()
                .build();
        this.abilityUseConditional = new AbilityConditional.Builder()
                .hasPermission()
                .hasCategory()
                .isEnabled()
                .build();
        this.abilityStorage = storage.getPlayerAbilityTables();
        this.presetStorage = storage.getPresetAbilityTables();
        this.uuid = livingEntity.getUniqueId().toString();
        this.asyncService = asyncService;
        this.loadAbilityStorageAsync();
        this.loadPresetStorageAsync();
        abilityInstanceService.createPassives(this);
    }

    @Override
    public boolean canUse(Location location) {
        return true;
    }

    public int getHeldItemSlot() {
        return getEntity().getInventory().getHeldItemSlot() + 1;
    }

    public boolean canBind(AbilityInformation ability) {
        return this.abilityBindConditional.matches(this, ability);
    }

    public boolean canUse(AbilityInformation ability) {
        return this.abilityUseConditional.matches(this, ability);
    }

    public boolean canUse(AbilityCategory category) {
        return this.categoryUseConditional.matches(this, category);
    }

    @Override
    public AbilityInformation getSelectedAbility() {
        return slotContainer.getAbility(getHeldItemSlot());
    }

    @Override
    public void updateAbilityBoard() {
        this.abilityBoard.update();
    }

    @Override
    public void enableAbilityBoard() {
        this.abilityBoard.setEnabled(true);
    }

    @Override
    public void disableAbilityBoard() {
        this.abilityBoard.setEnabled(false);
    }

    @Override
    public boolean isAbilityBoardEnabled() {
        return this.abilityBoard.isEnabled();
    }

    @Override
    public double getEnergy() {
        return config.getCastPreventType() == AbilityCastPreventType.COOLDOWN ? Double.MAX_VALUE : this.energy;
    }

    @Override
    public boolean removeEnergy(double value) {
        if (config.getCastPreventType() == AbilityCastPreventType.COOLDOWN) return true;
        if (this.energy < value) return false;
        this.setEnergy(this.energy - value);
        return true;
    }

    @Override
    public void addEnergy(double value) {
        this.setEnergy(this.energy + value);
    }

    @Override
    public void setEnergy(double value) {
        if (config.getCastPreventType() == AbilityCastPreventType.COOLDOWN) return;
//        AbilityUserEnergyChangeEvent event = new AbilityUserEnergyChangeEvent(this, this.energy, energy);
//        Bukkit.getPluginManager().callEvent(event);
//        this.energy = event.getNewEnergy();
        this.energy = Math.max(0, Math.min(value, getMaxEnergy()));
    }

    @Override
    public double getMaxEnergy() {
        if (config.getCastPreventType() == AbilityCastPreventType.COOLDOWN) return Double.MAX_VALUE;
//        AbilityUserCalculateMaxEnergyEvent event = new AbilityUserCalculateMaxEnergyEvent(this, config.getEnergyMax());
//        Bukkit.getPluginManager().callEvent(event);
//        return event.getMaxEnergy();
        return config.getEnergyMax();
    }

    @Override
    public void updateEnergyBar() {
        this.energyBar.update();
    }

    @Override
    public void enableEnergyBar() {
        this.energyBar.setEnabled(true);
    }

    @Override
    public void disableEnergyBar() {
        this.energyBar.setEnabled(false);
    }

    @Override
    public boolean isEnergyBarEnabled() {
        return this.energyBar.isEnabled();
    }

    @Override
    public Player getEntity() {
        return (Player) livingEntity;
    }

    @Override
    public void setAbility(int slot, AbilityInformation ability) {
        super.setAbility(slot, ability);
        this.saveAbilityStorageAsync();
    }

    @Override
    public void setSlotContainer(AbilitySlotContainer slotContainer) {
        super.setSlotContainer(slotContainer);
        slotContainer.validate(this);
        this.saveAbilityStorageAsync();
    }

    @Override
    public void clearAbilities() {
        super.clearAbilities();
    }

    public void saveAbilityStorageAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                abilityStorage.createOrUpdate(new PlayerAbilityTable(uuid, slotContainer.toString()));
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, executorService);
    }

    public void loadAbilityStorageAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                Optional.ofNullable(abilityStorage.queryForId(uuid)).ifPresent(playerAbilityTable -> this.setSlotContainer(DefaultAbilitySlotContainer.fromString(playerAbilityTable.getAbilities())));
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }, executorService);
    }

    @SuppressWarnings("unchecked")
    public void savePresetStorageAsync() {
        HashMap<String, String> presets = (HashMap<String, String>) this.presets.clone();
        asyncService.supplyAsync(() -> {
            while (true) {
                if (!presetsIsLocked) {
                    presetsIsLocked = true;
                    try {
                        this.presetStorage.delete(this.presetStorage.queryForFieldValues(Map.of("uuid", livingEntity.getUniqueId())));
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                    presets.forEach((key, value) -> {
                        try {
                            this.presetStorage.createOrUpdate(new PresetAbilityTable(this.uuid + "|" + key, this.uuid, key, value));
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        }
                    });
                    return true;
                }
            }
        }, (result) -> presetsIsLocked = false);
    }

    @SuppressWarnings("unchecked")
    public void loadPresetStorageAsync() {
        HashMap<String, String> presets = (HashMap<String, String>) this.presets.clone();
        asyncService.supplyAsync(() -> {
            while (true) {
                if (!presetsIsLocked) {
                    presetsIsLocked = true;
                    try {
                        this.presetStorage.queryForFieldValues(Map.of("uuid", livingEntity.getUniqueId())).forEach(table -> presets.put(table.getName(), table.getAbilities()));
                    } catch (SQLException exception) {
                        exception.printStackTrace();
                    }
                    return true;
                }
            }
        }, (result) -> {
            presetsIsLocked = false;
            this.presets.clear();
            this.presets.putAll(presets);
        });
    }

    public Map<String, String> getPresets() {
        return Collections.unmodifiableMap(presets);
    }

    public boolean bindPreset(String name) {
        if (presets.containsKey(name)) {
            this.setSlotContainer(DefaultAbilitySlotContainer.fromString(presets.get(name)));
            return true;
        }
        return false;
    }

    public boolean savePreset(String name) {
        if (presets.containsKey(name)) {
            return false;
        }
        presets.put(name, slotContainer.toString());
        savePresetStorageAsync();
        return true;
    }

    public boolean removePreset(String name) {
        if (presets.containsKey(name)) {
            presets.remove(name);
            savePresetStorageAsync();
            return true;
        }
        return false;
    }
}
