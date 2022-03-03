package com.github.vscyther.duelsplugin.listener;

import com.github.vscyther.duelsplugin.controller.ArenaController;
import com.github.vscyther.duelsplugin.controller.DuelController;
import com.github.vscyther.duelsplugin.controller.PlayerStatsController;
import com.github.vscyther.duelsplugin.model.Duel;
import com.github.vscyther.duelsplugin.model.PlayerStats;
import com.github.vscyther.duelsplugin.repository.PlayerStatsRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

@RequiredArgsConstructor
public class DuelListener implements Listener {

    private final ArenaController arenaController;
    private final DuelController duelController;
    private final PlayerStatsController playerStatsController;
    private final PlayerStatsRepository playerStatsRepository;

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        playerStatsRepository.load(PlayerStats.of(event.getPlayer().getUniqueId(),
                0,
                0,
                0,
                0,
                0));
    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent event) {
        playerStatsController.removeCache(event.getPlayer().getUniqueId());

        duelController.getInvitations().removeIf(invitation -> invitation.getDefied() == event.getPlayer()
                || invitation.getDefiant() == event.getPlayer());

        duelController.removePlayers(event.getPlayer());
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        final Player victim = event.getEntity();
        final Player killer = victim.getKiller();

        final Duel duel = duelController.getDuelByPlayers(victim, killer);
        if (duel == null || duel.isInQueue()) return;

        duel.stopTimer();

        duelController.removePlayers(killer);

        final PlayerStats victimStats = playerStatsController.getByUniqueId(victim.getUniqueId());
        victimStats.increaseDefeat();
        victimStats.setWinStreak(0);
        victimStats.increaseDeath();

        playerStatsRepository.update(victimStats);

        victim.teleport(victim == duel.getDefiant() ? duel.getDefiantOldLocation() : duel.getDefiedOldLocation());

        if (killer != null) {
            final PlayerStats killerStats = playerStatsController.getByUniqueId(killer.getUniqueId());
            killerStats.increaseWin();
            killerStats.increaseWinStreak();
            killerStats.increaseKill();

            playerStatsRepository.update(killerStats);

            killer.teleport(killer == duel.getDefiant() ? duel.getDefiantOldLocation() : duel.getDefiedOldLocation());
        }

        arenaController.setInUse(false);
        arenaController.setDuel(null);
    }

    @EventHandler
    void onEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) && !(event.getDamager() instanceof Player))
            return;

        final Player victim = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        final Duel duel = duelController.getDuelByPlayers(victim, damager);
        if (duel == null || duel.isInQueue()) return;

        if (duel.isStarting()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().getX() != Objects.requireNonNull(event.getTo()).getX()
                || event.getFrom().getY() != event.getTo().getY()
                || event.getFrom().getZ() != event.getTo().getZ()) {
            final Duel duel = duelController.getDuelByPlayer(event.getPlayer());
            if (duel == null || duel.isInQueue()) return;

            if (duel.isStarting())
                event.getPlayer().teleport(event.getFrom());
        }
    }

}