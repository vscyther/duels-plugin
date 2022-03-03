package com.github.vscyther.duelsplugin.task;

import com.github.vscyther.duelsplugin.DuelsPlugin;
import com.github.vscyther.duelsplugin.controller.ArenaController;
import com.github.vscyther.duelsplugin.controller.DuelController;
import com.github.vscyther.duelsplugin.message.MessageProvider;
import com.github.vscyther.duelsplugin.model.Duel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

@RequiredArgsConstructor
public class DuelQueue implements Runnable {

    private final MessageProvider messageProvider;
    private final ArenaController arenaController;
    private final DuelController duelController;

    @Override
    public void run() {
        if (arenaController.isInUse() && arenaController.getDuel() != null)
            return;

        final Duel duel = duelController.getNextDuel();
        duel.setInQueue(false);
        duel.setStarting(true);

        duel.getDefiant().sendMessage(messageProvider.getMessage("queue-moved"));
        duel.getDefied().sendMessage(messageProvider.getMessage("queue-moved"));

        Bukkit.getScheduler().runTaskLater(DuelsPlugin.getInstance(), () -> duelController.starting(duel), 200L);
    }

}