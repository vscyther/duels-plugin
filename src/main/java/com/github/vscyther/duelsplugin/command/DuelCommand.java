package com.github.vscyther.duelsplugin.command;

import com.github.vscyther.duelsplugin.controller.ArenaController;
import com.github.vscyther.duelsplugin.controller.DuelController;
import com.github.vscyther.duelsplugin.controller.KitController;
import com.github.vscyther.duelsplugin.controller.PlayerStatsController;
import com.github.vscyther.duelsplugin.message.MessageProvider;
import com.github.vscyther.duelsplugin.model.Kit;
import com.github.vscyther.duelsplugin.model.PlayerStats;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class DuelCommand implements CommandExecutor {

    private final MessageProvider messageProvider;
    private final KitController kitController;
    private final ArenaController arenaController;
    private final DuelController duelController;
    private final PlayerStatsController playerStatsController;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(messageProvider.getMessage("only-players"));
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(messageProvider.getMessageList("help").toArray(new String[0]));
            return false;
        }

        if (args[0].equalsIgnoreCase("arena")) {
            if (!player.hasPermission("duels.admin")) {
                player.sendMessage(messageProvider.getMessage("no-permission"));
                return false;
            }

            if (args[1].equalsIgnoreCase("setpos1")) {
                arenaController.setPosition(1, player, messageProvider.getMessage("position-defined"));
                return true;
            }

            if (args[1].equalsIgnoreCase("setpos2")) {
                arenaController.setPosition(2, player, messageProvider.getMessage("position-defined"));
                return true;
            }

            player.sendMessage(messageProvider.getMessage("syntax-error")
                    .replace("%usage%", "duel arena <setpos1/setpos2>"));
            return false;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length != 2) {
                player.sendMessage(messageProvider.getMessage("syntax-error")
                        .replace("%usage%", "duel accept <player name>"));
                return false;
            }

            final Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(messageProvider.getMessage("offline-player"));
                return false;
            }

            duelController.accept(target, player);
            return true;
        }

        if (args[0].equalsIgnoreCase("stats")) {
            if (args.length == 2) {
                final PlayerStats playerStats = playerStatsController.getByUniqueId(player.getUniqueId());

                player.sendMessage(messageProvider.getMessageList("your-status").stream()
                        .map($ -> $.replace("%wins%", String.valueOf(playerStats.getWins()))
                                .replace("%defeats%", String.valueOf(playerStats.getDefeats()))
                                .replace("%win_streak%", String.valueOf(playerStats.getWinStreak()))
                                .replace("%kills%", String.valueOf(playerStats.getKills()))
                                .replace("%deaths%", String.valueOf(playerStats.getDeaths())))
                        .toArray(String[]::new));
                return true;
            }

            if (args.length == 3) {
                final Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage(messageProvider.getMessage("offline-player"));
                    return false;
                }

                final PlayerStats targetStats = playerStatsController.getByUniqueId(player.getUniqueId());

                player.sendMessage(messageProvider.getMessageList("your-status").stream()
                        .map($ -> $.replace("%wins%", String.valueOf(targetStats.getWins()))
                                .replace("%defeats%", String.valueOf(targetStats.getDefeats()))
                                .replace("%win_streak%", String.valueOf(targetStats.getWinStreak()))
                                .replace("%kills%", String.valueOf(targetStats.getKills()))
                                .replace("%deaths%", String.valueOf(targetStats.getDeaths())))
                        .toArray(String[]::new));
                return true;
            }

            player.sendMessage(messageProvider.getMessage("syntax-error")
                    .replace("%usage%", "duel stats [player name]"));
            return false;
        }

        final Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(messageProvider.getMessage("offline-player"));
            return false;
        }

        final Kit kit = args.length == 2 ? kitController.getKitByName(args[1]) : kitController.getDefaultKit();

        if (kit == null) {
            final String kits = String.join("§7, ", kitController.getKitMap().keySet()) + "§7.";
            player.sendMessage(messageProvider.getMessage("invalid-kit").replace("%kits%", kits));
            return false;
        }

        duelController.invite(player, target, kit);
        return true;
    }

}