package com.github.vscyther.duelsplugin.model;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data(staticConstructor = "of")
public class Kit {

    private final String name;
    private final ItemStack[] armorContent, inventoryContent;

}