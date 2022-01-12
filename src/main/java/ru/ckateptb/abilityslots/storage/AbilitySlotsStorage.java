package ru.ckateptb.abilityslots.storage;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import ru.ckateptb.abilityslots.AbilitySlots;
import ru.ckateptb.abilityslots.config.AbilitySlotsConfig;
import ru.ckateptb.tablecloth.storage.hikari.HikariConfig;
import ru.ckateptb.tablecloth.storage.hikari.HikariDataSource;
import ru.ckateptb.tablecloth.storage.ormlite.dao.Dao;
import ru.ckateptb.tablecloth.storage.ormlite.dao.DaoManager;
import ru.ckateptb.tablecloth.storage.ormlite.jdbc.DataSourceConnectionSource;
import ru.ckateptb.tablecloth.storage.ormlite.support.ConnectionSource;
import ru.ckateptb.tablecloth.storage.ormlite.table.TableUtils;

import javax.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

@Slf4j
@Getter
@Component
public class AbilitySlotsStorage implements AutoCloseable {
    private final AbilitySlotsConfig config;
    private ConnectionSource connection;
    private Dao<PlayerAbilityTable, String> playerAbilityTables;
    private Dao<PresetAbilityTable, String> presetAbilityTables;

    public AbilitySlotsStorage(AbilitySlotsConfig config) {
        this.config = config;
    }

    @SneakyThrows
    @PostConstruct
    public void init() {
        this.connection = createConnection(config);
        TableUtils.createTableIfNotExists(connection, PlayerAbilityTable.class);
        TableUtils.createTableIfNotExists(connection, PresetAbilityTable.class);
        this.playerAbilityTables = DaoManager.createDao(connection, PlayerAbilityTable.class);
        this.presetAbilityTables = DaoManager.createDao(connection, PresetAbilityTable.class);
    }

    @SneakyThrows
    private ConnectionSource createConnection(AbilitySlotsConfig config) {
        HikariDataSource dataSource;
        boolean mysqlEnable = config.isMysqlEnable();
        if (mysqlEnable) {
            dataSource = createMySqlDataSource();
        } else {
            dataSource = createSqliteDatasource();
        }
        try {
            return new DataSourceConnectionSource(dataSource, dataSource.getJdbcUrl());
        } catch (SQLException exception) {
            log.error("Failed to connect to database", exception);
            if (mysqlEnable) {
                log.warn("Perhaps you did not configure MySQL correctly or it is not available");
                log.warn("Switching to SQLite");
                dataSource = createSqliteDatasource();
                return new DataSourceConnectionSource(dataSource, dataSource.getJdbcUrl());
            } else {
                throw exception;
            }
        }
    }

    @SneakyThrows
    private HikariDataSource createSqliteDatasource() {
        Path path = Paths.get(AbilitySlots.getInstance().getDataFolder().getPath(), "storage.db");
        FileUtils.forceMkdirParent(path.toFile());
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + path);
        config.setConnectionTimeout(0);
        return new HikariDataSource(config);
    }

    private HikariDataSource createMySqlDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        String jdbcUrl = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8";
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setJdbcUrl(String.format(jdbcUrl, config.getMysqlHost(), config.getMysqlPort(), config.getMysqlDatabase()));
        hikariConfig.setUsername(config.getMysqlUsername());
        hikariConfig.setPassword(config.getMysqlPassword());
        return new HikariDataSource(hikariConfig);
    }

    @Override
    public void close() throws Exception {
        connection.close();
    }
}
