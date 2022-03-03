package com.github.vscyther.duelsplugin.model;

import com.github.vscyther.duelsplugin.DuelsPlugin;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@AllArgsConstructor
public class Duel {

    private final Player defiant, defied;
    private final Location defiantOldLocation, defiedOldLocation;
    private final Kit kit;
    private boolean starting, inQueue;

    private BukkitTask bukkitTask;

    public static Duel of(Player defiant,
                          Player defied,
                          Location defiantOldLocation,
                          Location defiedOldLocation,
                          Kit kit,
                          boolean starting,
                          boolean inQueue) {
        return new Duel(defiant,
                defied,
                defiantOldLocation,
                defiedOldLocation,
                kit,
                starting,
                inQueue,
                null);
    }

    public void equipFighters() {
        defiant.getInventory().setArmorContents(kit.getArmorContent());
        defiant.getInventory().setContents(kit.getInventoryContent());

        defied.getInventory().setArmorContents(kit.getArmorContent());
        defied.getInventory().setContents(kit.getInventoryContent());
    }

    public void startTimer() {
        final AtomicInteger seconds = new AtomicInteger(0);

        bukkitTask = Bukkit.getScheduler().runTaskTimer(DuelsPlugin.getInstance(), () -> {
            sendActionBar(defiant, seconds.get());
            sendActionBar(defied, seconds.get());

            seconds.getAndIncrement();
        }, 20L, 20L);
    }

    public void stopTimer() {
        bukkitTask.cancel();
    }

    private void sendActionBar(Player player, int seconds) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                new TextComponent("§c" + chronometerFormatter(seconds)));
    }

    private String chronometerFormatter(int time) {
        return String.format("%02d:%02d", time / 60, time % 60);
    }

}