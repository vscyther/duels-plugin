package com.github.vscyther.duelsplugin.message;

import com.google.common.collect.Maps;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MessageProvider {

    private final Map<String, Object> messages = Maps.newHashMap();

    public void init(JavaPlugin plugin) {
        final ConfigurationSection section = plugin.getConfig().getConfigurationSection("messages");
        if (section == null)
            return;

        for (String key : section.getKeys(false)) {
            final Object value = plugin.getConfig().get("messages." + key);

            messages.put(key, value instanceof String
                    ? colorize((String) value)
                    : colorize((List<String>) value));
        }
    }

    public String getMessage(String key) {
        return (String) messages.get(key);
    }

    public List<String> getMessageList(String key) {
        return (List<String>) messages.get(key);
    }

    private String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private List<String> colorize(List<String> list) {
        return list.stream().map(this::colorize).collect(Collectors.toList());
    }

}