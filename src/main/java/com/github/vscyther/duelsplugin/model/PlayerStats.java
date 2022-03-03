package com.github.vscyther.duelsplugin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PlayerStats {

    private final UUID uniqueId;
    private int wins, defeats, winStreak;
    private int kills, deaths;

    public static PlayerStats of(UUID uniqueId, int wins, int defeats, int winStreak, int kills, int deaths) {
        return new PlayerStats(uniqueId, wins, defeats, winStreak, kills, deaths);
    }

    public Player asPlayer() {
        return Bukkit.getPlayer(uniqueId);
    }

    public void increaseWin() {
        ++wins;
    }

    public void increaseDefeat() {
        ++defeats;
    }

    public void increaseWinStreak() {
        ++winStreak;
    }

    public void increaseKill() {
        ++kills;
    }

    public void increaseDeath() {
        ++deaths;
    }

}