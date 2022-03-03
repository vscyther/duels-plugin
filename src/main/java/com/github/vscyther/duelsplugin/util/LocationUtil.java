package com.github.vscyther.duelsplugin.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

public class LocationUtil {

    public static String serialize(Location location, boolean yawAndPitch) {
        if (location.getWorld() == null)
            return null;

        final StringJoiner joiner = new StringJoiner(";");
        joiner.add(location.getWorld().getName());
        joiner.add(String.valueOf(location.getX()));
        joiner.add(String.valueOf(location.getY()));
        joiner.add(String.valueOf(location.getZ()));

        if (yawAndPitch) {
            joiner.add(String.valueOf(location.getYaw()));
            joiner.add(String.valueOf(location.getPitch()));
        }

        return joiner.toString().substring(0, joiner.length() - 1);
    }

    public static Location deserialize(String serializedLocation) {
        final String[] location = serializedLocation.split(";");
        if (location.length == 4) {
            return new Location(
                    Bukkit.getWorld(location[0]),
                    Double.parseDouble(location[1]),
                    Double.parseDouble(location[2]),
                    Double.parseDouble(location[3])
            );
        }

        if (location.length == 6) {
            return new Location(
                    Bukkit.getWorld(location[0]),
                    Double.parseDouble(location[1]),
                    Double.parseDouble(location[2]),
                    Double.parseDouble(location[3]),
                    Float.parseFloat(location[4]),
                    Float.parseFloat(location[5])
            );
        }

        throw new IllegalArgumentException("This string cannot be deserialized!");
    }

}