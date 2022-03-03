package com.github.vscyther.duelsplugin.loader;

import com.github.vscyther.duelsplugin.controller.KitController;
import com.github.vscyther.duelsplugin.model.Kit;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
public class KitLoader {

    private final KitController kitController;

    public void init(JavaPlugin plugin) {
        final File[] files = new File(plugin.getDataFolder(), "kits").listFiles();
        if (files == null)
            return;

        for (File file : files) {
            final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            final ConfigurationSection section = yamlConfiguration.getConfigurationSection("kit");

            if (section == null)
                continue;

            final List<ItemStack> armorContent = Lists.newLinkedList();

            final ConfigurationSection armorSection = section.getConfigurationSection("armor-content");
            if (armorSection != null) {
                for (String key : armorSection.getKeys(false)) {
                    final ConfigurationSection value = armorSection.getConfigurationSection(key);
                    if (value != null) {
                        final Material material = Material.getMaterial(value.getString("material", "BARRIER"));
                        final int amount = value.getInt("amount", 1);
                        final int durability = value.getInt("durability", 0);

                        armorContent.add(new ItemStack(material == null ? Material.BARRIER : material,
                                amount,
                                (short) durability));
                    }
                }
            }

            final List<ItemStack> inventoryContent = Lists.newLinkedList();

            final ConfigurationSection inventorySection = section.getConfigurationSection("inventory-content");
            if (inventorySection != null) {
                for (String key : inventorySection.getKeys(true)) {
                    final ConfigurationSection value = inventorySection.getConfigurationSection(key);
                    if (value != null) {
                        final Material material = Material.getMaterial(value.getString("material", "BARRIER"));
                        final int amount = value.getInt("amount", 1);
                        final int durability = value.getInt("durability", 0);

                        inventoryContent.add(new ItemStack(material == null ? Material.BARRIER : material,
                                amount,
                                (short) durability));
                    }
                }
            }

            final String name = section.getString("name");

            kitController.getKitMap().put(name, Kit.of(name,
                    armorContent.toArray(new ItemStack[0]),
                    inventoryContent.toArray(new ItemStack[0])));
        }

        System.out.println("[duels-plugin] " + kitController.getKitMap().size()
                + String.format(" kit%s were uploaded.", kitController.getKitMap().size() > 1 ? "s" : ""));

        final Kit defaultKit = kitController.getKitByName(plugin.getConfig().getString("default-kit"));
        if (defaultKit == null) {
            System.out.println("[duels-plugin] Unable to define a default kit, " +
                    "the kit defined in the configuration is invalid.");
            return;
        }

        kitController.setDefaultKit(defaultKit);

        System.out.printf("[duels-plugin] The '%s' kit has been set as the default kit!%n", defaultKit.getName());
    }

}