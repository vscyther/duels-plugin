package com.github.vscyther.duelsplugin.controller;

import com.github.vscyther.duelsplugin.model.Kit;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Getter
public class KitController {

    private final Map<String, Kit> kitMap = Maps.newHashMap();

    public Kit getKitByName(String kitName) {
        return kitMap.get(kitName);
    }

    @Setter
    private Kit defaultKit;

    public void init(JavaPlugin plugin) {
        final File folder = new File(plugin.getDataFolder(), "kits");
        if (!folder.exists())
            folder.mkdirs();

        if (plugin.getConfig().getBoolean("create-example-kit-file")) {
            final File exampleKitFile = new File(plugin.getDataFolder() + "/kits/example_kit.yml");
            if (!exampleKitFile.exists()) {
                try {
                    exampleKitFile.createNewFile();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }

            plugin.saveResource("kits/example_kit.yml", true);
        }
    }

}