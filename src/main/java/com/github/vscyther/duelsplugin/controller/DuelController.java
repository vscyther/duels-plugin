package com.github.vscyther.duelsplugin.controller;

import com.github.vscyther.duelsplugin.DuelsPlugin;
import com.github.vscyther.duelsplugin.message.MessageProvider;
import com.github.vscyther.duelsplugin.model.Duel;
import com.github.vscyther.duelsplugin.model.Invitation;
import com.github.vscyther.duelsplugin.model.Kit;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@RequiredArgsConstructor
public class DuelController {

    private final Map<Long, Duel> duels = Maps.newLinkedHashMap();
    private final Set<Invitation> invitations = Sets.newConcurrentHashSet();

    private final MessageProvider messageProvider;
    private final ArenaController arenaController;

    public void invite(Player defiant, Player defied, Kit kit) {
        if (hasPendingInvitation(defiant) || hasPendingInvitation(defied)) {
            defiant.sendMessage(messageProvider.getMessage("has-a-pending-invitation"));
            return;
        }

        defiant.sendMessage(messageProvider.getMessage("invitation-sent")
                .replace("%player%", defied.getName()));

        defied.sendMessage(messageProvider.getMessageList("invitation-received").stream()
                .map($ -> $.replace("%player%", defiant.getName()).replace("%kit%", kit.getName()))
                .toArray(String[]::new));

        invitations.add(Invitation.of(defiant, defied, kit));

        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), () -> {
            if (!hasPendingInvitation(defiant) && !hasPendingInvitation(defied))
                return;

            invitations.removeIf(invitation -> invitation.getDefiant() == defiant);

            defiant.sendMessage(messageProvider.getMessage("expired-invitation"));
            defied.sendMessage(messageProvider.getMessage("expired-invitation"));
        }, 1200L);
    }

    public void accept(Player defiant, Player defied) {
        if (defiant == defied) {
            defied.sendMessage(messageProvider.getMessage("thats-you"));
            return;
        }

        final Invitation invitation = invitations.stream()
                .filter(i -> i.getDefiant() == defiant && i.getDefied() == defied)
                .findFirst()
                .orElse(null);

        if (invitation == null) {
            defied.sendMessage(messageProvider.getMessage("no-duel"));
            return;
        }

        defiant.sendMessage(messageProvider.getMessage("duel-accepted")
                .replace("%player%", defied.getName()));

        defied.sendMessage(messageProvider.getMessage("duel-accepted")
                .replace("%player%", defiant.getName()));

        final Duel duel = Duel.of(defiant,
                defied,
                defiant.getLocation(),
                defied.getLocation(),
                invitation.getKit(),
                true,
                arenaController.isInUse());

        duels.put(System.currentTimeMillis(), duel);

        if (arenaController.isInUse()) {
            defiant.sendMessage(messageProvider.getMessage("arena-in-use"));
            defied.sendMessage(messageProvider.getMessage("arena-in-use"));
        } else {
            starting(duel);
        }
    }

    public void starting(Duel duel) {
        arenaController.setInUse(true);
        arenaController.setDuel(duel);
        arenaController.teleport(duel.getDefiant(), duel.getDefied());

        duel.equipFighters();

        final Player defiant = duel.getDefiant();
        final Player defied = duel.getDefied();

        final AtomicInteger seconds = new AtomicInteger(5);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (seconds.get() == 0) {
                    cancel();

                    duel.setStarting(false);
                    duel.startTimer();

                    defiant.sendMessage(messageProvider.getMessage("duel-started"));
                    defied.sendMessage(messageProvider.getMessage("duel-started"));
                    return;
                }

                defiant.sendMessage(messageProvider.getMessage("duel-starting")
                        .replace("%seconds%", String.valueOf(seconds.get())));

                defied.sendMessage(messageProvider.getMessage("duel-starting")
                        .replace("%seconds%", String.valueOf(seconds.get())));

                seconds.getAndDecrement();
            }
        }.runTaskTimer(DuelsPlugin.getInstance(), 20L, 20L);
    }

    public Duel getDuelByPlayers(Player victim, Player damager) {
        for (Duel duel : duels.values()) {
            if (duel.getDefiant() == victim || duel.getDefied() == victim
                    && duel.getDefiant() == damager || duel.getDefied() == damager) {
                return duel;
            }
        }

        return null;
    }

    public Duel getDuelByPlayer(Player player) {
        for (Duel duel : duels.values()) {
            if (duel.getDefiant() == player || duel.getDefied() == player) {
                return duel;
            }
        }

        return null;
    }

    public void removePlayers(Player player) {
        final Iterator<Map.Entry<Long, Duel>> iterator = duels.entrySet().iterator();
        while (iterator.hasNext()) {
            final Duel duel = iterator.next().getValue();
            if (duel.getDefiant() == player || duel.getDefied() == player) {
                iterator.remove();
                break;
            }
        }
    }

    public Duel getNextDuel() {
        return duels.values().iterator().next();
    }

    private boolean hasPendingInvitation(Player player) {
        for (Invitation invitation : invitations) {
            if (invitation.getDefiant() == player || invitation.getDefied() == player) {
                return true;
            }
        }

        return false;
    }

}