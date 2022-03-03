package com.github.vscyther.duelsplugin.repository;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@RequiredArgsConstructor
public class SQLDatabase {

    private final AtomicInteger pollSize = new AtomicInteger(1);

    private final HikariDataSource dataSource = new HikariDataSource();

    protected final JavaPlugin plugin;

    public void init() {
        final ConfigurationSection section = plugin.getConfig().getConfigurationSection("mysql");

        dataSource.setPoolName("slay-helper:" + pollSize.getAndIncrement());

        dataSource.setUsername(section.getString("username"));
        dataSource.setPassword(section.getString("password"));

        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");

        dataSource.setJdbcUrl(String.format("jdbc:mariadb://%s/%s",
                section.getString("address"),
                section.getString("database")));
    }

    public void shutdown() {
        if (!dataSource.isRunning() || !dataSource.isClosed())
            return;

        dataSource.close();
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException exception) {
            return null;
        }
    }

}