package ru.ckateptb.abilityslots.config;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
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

    @ConfigField(name = "global.castPreventType", comment = "Available types: COOLDOWN (the user can use the ability while it is not on cooldown), ENERGY (the user can use the abilities as long as he has a energy to use), MIXED (use both options)")
    private String castPreventType = AbilityCastPreventType.MIXED.name();
    @ConfigField(name = "global.energy.name")
    private String energyName = "§5Ability Energy";
    @ConfigField(name = "global.energy.color", comment = "Available types: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
    private String energyColor = BarColor.YELLOW.name();
    @ConfigField(name = "global.energy.style", comment = "Available types: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20")
    private String energyStyle = BarStyle.SOLID.name();
    @ConfigField(name = "global.energy.max", comment = "Base resource limit")
    private double energyMax = 100;
    @ConfigField(name = "global.energy.regen", comment = "The amount of resource that will be restored every second")
    private double energyRegen = 5;

    @ConfigField(name = "global.board.enabled", comment = "Displaying current abilities in the scoreboard")
    private boolean boardEnabled = true;
    @ConfigField(name = "global.board.header")
    private String boardHeader = "§f§lAbilities:";
    @ConfigField(name = "global.board.emptySlot")
    private String boardEmptySlot = "§8-- Empty --";
    @ConfigField(name = "global.board.abilitySlot")
    private String boardAbilitySlot = "%slot%";
    @ConfigField(name = "global.board.comboDelimiter")
    private String boardComboDelimiter = "§f§lCombos:";

    @Override
    public void init() {
    }

    public AbilityCastPreventType getCastPreventType() {
        return AbilityCastPreventType.valueOf(castPreventType);
    }

    public BarColor getEnergyColor() {
        return BarColor.valueOf(energyColor);
    }

    public BarStyle getEnergyStyle() {
        return BarStyle.valueOf(energyStyle);
    }

    @Override
    public Plugin getPlugin() {
        return AbilitySlots.getInstance();
    }
}
