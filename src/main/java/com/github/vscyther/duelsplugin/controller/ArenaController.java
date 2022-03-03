package com.github.vscyther.duelsplugin.controller;

import com.github.vscyther.duelsplugin.model.Duel;
import com.github.vscyther.duelsplugin.util.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public class ArenaController {

    private JavaPlugin plugin;

    private Location position1, position2;

    @Setter
    private boolean inUse;

    @Setter
    private Duel duel;

    public void init(JavaPlugin plugin) {
        this.plugin = plugin;

        final FileConfiguration config = plugin.getConfig();

        final String pos1 = config.getString("arena.position-1");
        final String pos2 = config.getString("arena.position-2");

        if (!Objects.requireNonNull(pos1).equalsIgnoreCase("empty"))
            position1 = LocationUtil.deserialize(pos1);

        if (!Objects.requireNonNull(pos2).equalsIgnoreCase("empty"))
            position2 = LocationUtil.deserialize(pos2);
    }

    public void setPosition(int position, Player player, String message) {
        final Location location = player.getLocation();

        if (position == 1) {
            position1 = location;
        } else if (position == 2) {
            position2 = location;
        }

        plugin.getConfig().set("arena.position-" + position, LocationUtil.serialize(location, true));
        plugin.saveConfig();

        player.sendMessage(message.replace("%position%", String.valueOf(position)));
    }

    public void teleport(Player defiant, Player defied) {
        defiant.teleport(position1);
        defied.teleport(position2);
    }

}