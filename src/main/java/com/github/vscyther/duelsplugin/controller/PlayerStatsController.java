package com.github.vscyther.duelsplugin.controller;

import com.github.vscyther.duelsplugin.model.PlayerStats;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

public class PlayerStatsController {

    @Getter
    private final Map<UUID, PlayerStats> statsCache = Maps.newHashMap();

    public void insertCache(PlayerStats playerStats) {
        statsCache.put(playerStats.getUniqueId(), playerStats);
    }

    public void removeCache(UUID uniqueId) {
        statsCache.remove(uniqueId);
    }

    public PlayerStats getByUniqueId(UUID uniqueId) {
        return statsCache.get(uniqueId);
    }

}