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

    @Override
    public void init() {
    }

    @Override
    public Plugin getPlugin() {
        return AbilitySlots.getInstance();
    }
}
