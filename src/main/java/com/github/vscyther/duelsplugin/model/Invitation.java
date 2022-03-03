package com.github.vscyther.duelsplugin.model;

import lombok.Data;
import org.bukkit.entity.Player;

@Data(staticConstructor = "of")
public class Invitation {

    private final Player defiant, defied;
    private final Kit kit;

}