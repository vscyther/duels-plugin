package com.github.vscyther.duelsplugin.repository;

import com.github.vscyther.duelsplugin.DuelsPlugin;
import com.github.vscyther.duelsplugin.controller.PlayerStatsController;
import com.github.vscyther.duelsplugin.model.PlayerStats;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class PlayerStatsRepository {

    private final SQLDatabase sqlDatabase;
    private final PlayerStatsController playerStatsController;

    public void createTable() {
        try (final Connection connection = sqlDatabase.getConnection()) {
            try (final PreparedStatement statement = connection.prepareStatement(SQLConstants.CREATE_TABLE)) {
                statement.execute();
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void load(PlayerStats playerStats) {
        playerStatsController.insertCache(playerStats);

        Bukkit.getScheduler().runTaskAsynchronously(DuelsPlugin.getInstance(), () -> {
            try (final Connection connection = sqlDatabase.getConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement(SQLConstants.SELECT_QUERY)) {
                    statement.setString(1, playerStats.getUniqueId().toString());

                    try (final ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            playerStats.setWins(resultSet.getInt("wins"));
                            playerStats.setDefeats(resultSet.getInt("defeats"));
                            playerStats.setWinStreak(resultSet.getInt("winStreak"));
                            playerStats.setKills(resultSet.getInt("kills"));
                            playerStats.setDeaths(resultSet.getInt("deaths"));
                        }
                    }
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public void update(PlayerStats playerStats) {
        Bukkit.getScheduler().runTaskAsynchronously(DuelsPlugin.getInstance(), () -> {
            try (final Connection connection = sqlDatabase.getConnection()) {
                try (final PreparedStatement statement = connection.prepareStatement(SQLConstants.UPDATE_QUERY)) {
                    statement.setString(1, playerStats.getUniqueId().toString());
                    statement.setInt(2, playerStats.getWins());
                    statement.setInt(3, playerStats.getDefeats());
                    statement.setInt(4, playerStats.getWinStreak());
                    statement.setInt(5, playerStats.getKills());
                    statement.setInt(6, playerStats.getDeaths());
                    statement.setInt(7, playerStats.getWins());
                    statement.setInt(8, playerStats.getDefeats());
                    statement.setInt(9, playerStats.getWinStreak());
                    statement.setInt(10, playerStats.getKills());
                    statement.setInt(11, playerStats.getDeaths());

                    statement.executeUpdate();
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public static class SQLConstants {

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS duels (" +
                "id INTEGER NOT NULL AUTO_INCREMENT, " +
                "uniqueId CHAR(36) NOT NULL UNIQUE, " +
                "wins SMALLINT NOT NULL DEFAULT 0, " +
                "defeats SMALLINT NOT NULL DEFAULT 0, " +
                "winStreak TINYINT NOT NULL DEFAULT 0, " +
                "kills SMALLINT NOT NULL DEFAULT 0, " +
                "deaths SMALLINT NOT NULL DEFAULT 0, " +
                "PRIMARY KEY (id));";

        public static final String SELECT_QUERY = "SELECT * FROM duels WHERE uniqueId = ?;";

        public static final String UPDATE_QUERY = "INSERT INTO duels " +
                "(uniqueId, wins, defeats, winStreak, kills, deaths) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE wins = ?, defeats = ?, winStreak = ?, kills = ?, deaths = ?;";

    }

}