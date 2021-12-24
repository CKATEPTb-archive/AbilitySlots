package ru.ckateptb.abilityslots.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;
import org.springframework.stereotype.Component;
import ru.ckateptb.abilityslots.AbilitySlots;
import ru.ckateptb.tablecloth.config.ConfigField;
import ru.ckateptb.tablecloth.config.YamlConfig;

@Getter
@Setter
@Component
public class AbilitySlotsConfig extends YamlConfig {
    @ConfigField(name = "database.mysql.enabled")
    private boolean mysqlEnable = false;
    @ConfigField(name = "database.mysql.host")
    private String mysqlHost = "localhost";
    @ConfigField(name = "database.mysql.port")
    private String mysqlPort = "3306";
    @ConfigField(name = "database.mysql.database")
    private String mysqlDatabase = "database";
    @ConfigField(name = "database.mysql.username")
    private String mysqlUsername = "username";
    @ConfigField(name = "database.mysql.password")
    private String mysqlPassword = "password";

    @ConfigField(name = "global.cast-prevent-type", comment = "Available types: COOLDOWN (the user can use the ability while it is not on cooldown), ENERGY (the user can use the abilities as long as he has a energy to use), MIXED (use both options)")
    private String castPreventType = AbilityCastPreventType.MIXED.name();
    @ConfigField(name = "global.energy.name")
    private String energyName = "§5Ability Energy";
    @ConfigField(name = "global.energy.max", comment = "Base resource limit")
    private double energyMax = 100;
    @ConfigField(name = "global.energy.regen", comment = "The amount of resource that will be restored every second")
    private double energyRegen = 5;

    @ConfigField(name = "global.board.enabled", comment = "Displaying current abilities in the scoreboard")
    private boolean boardEnabled = true;
    @ConfigField(name = "global.board.header")
    private String boardHeader = "§f§lAbilities:";
    @ConfigField(name = "global.board.empty-slot")
    private String boardEmptySlot = "§8-- Empty --";
    @ConfigField(name = "global.board.ability-slot")
    private String boardAbilitySlot = "%slot%";
    @ConfigField(name = "global.board.combo-delimiter")
    private String boardComboDelimiter = "§f§lCombos:";

    @Override
    public void init() {
    }

    public AbilityCastPreventType getCastPreventType() {
        return AbilityCastPreventType.valueOf(castPreventType);
    }

    @Override
    public Plugin getPlugin() {
        return AbilitySlots.getInstance();
    }
}
