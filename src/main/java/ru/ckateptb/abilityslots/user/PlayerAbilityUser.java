package ru.ckateptb.abilityslots.user;

import lombok.Getter;
import org.bukkit.entity.Player;
import ru.ckateptb.abilityslots.ability.conditional.*;
import ru.ckateptb.abilityslots.ability.info.AbilityInformation;
import ru.ckateptb.abilityslots.board.AbilityBoard;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.category.conditional.PermissionCategoryConditional;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.abilityslots.energy.EnergyBar;
import ru.ckateptb.abilityslots.service.AbilityInstanceService;
import ru.ckateptb.abilityslots.service.AbilityService;
import ru.ckateptb.abilityslots.slot.AbilitySlotContainer;
import ru.ckateptb.abilityslots.slot.DefaultAbilitySlotContainer;
import ru.ckateptb.abilityslots.storage.AbilitySlotsStorage;
import ru.ckateptb.abilityslots.storage.PlayerAbilityTable;
import ru.ckateptb.tablecloth.storage.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerAbilityUser extends LivingEntityAbilityUser {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private final AbilityBoard abilityBoard;
    @Getter
    private final EnergyBar energyBar;
    private final CompositeAbilityConditional abilityBindConditional = new CompositeAbilityConditional();
    private final CompositeAbilityConditional abilityUseConditional = new CompositeAbilityConditional();
    private final PermissionCategoryConditional categoryUseConditional = new PermissionCategoryConditional();
    private final Dao<PlayerAbilityTable, String> abilityStorage;
    private final String uuid;

    public PlayerAbilityUser(Player livingEntity, AbilitySlotsConfig config, AbilityService abilityService, AbilityInstanceService abilityInstanceService, AbilitySlotsStorage storage) {
        super(livingEntity);
        this.abilityBoard = new AbilityBoard(this, config, abilityService);
        this.energyBar = new EnergyBar(this, config);
        this.abilityBindConditional.add(
                new CategoryAbilityConditional(),
                new EnabledAbilityConditional(),
                new CanBindToSlotAbilityConditional(),
                new PermissionAbilityConditional()
        );
        this.abilityUseConditional.add(
                new CategoryAbilityConditional(),
                new EnabledAbilityConditional(),
                new PermissionAbilityConditional()
        );
        this.abilityStorage = storage.getPlayerAbilityTables();
        this.uuid = livingEntity.getUniqueId().toString();
        this.loadAbilityStorageAsync();
        abilityInstanceService.createPassives(this);
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
        return super.getEnergy();
    }

    @Override
    public void setEnergy(double value) {
        super.setEnergy(value);
    }

    @Override
    public double getMaxEnergy() {
        return super.getMaxEnergy();
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
}
