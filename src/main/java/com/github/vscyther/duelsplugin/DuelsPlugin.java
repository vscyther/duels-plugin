package com.github.vscyther.duelsplugin;

import com.github.vscyther.duelsplugin.command.DuelCommand;
import com.github.vscyther.duelsplugin.controller.ArenaController;
import com.github.vscyther.duelsplugin.controller.DuelController;
import com.github.vscyther.duelsplugin.controller.KitController;
import com.github.vscyther.duelsplugin.controller.PlayerStatsController;
import com.github.vscyther.duelsplugin.listener.DuelListener;
import com.github.vscyther.duelsplugin.loader.KitLoader;
import com.github.vscyther.duelsplugin.message.MessageProvider;
import com.github.vscyther.duelsplugin.repository.PlayerStatsRepository;
import com.github.vscyther.duelsplugin.repository.SQLDatabase;
import com.github.vscyther.duelsplugin.task.DuelQueue;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public final class DuelsPlugin extends JavaPlugin {

    private SQLDatabase sqlDatabase;

    private MessageProvider messageProvider;

    private KitController kitController;
    private KitLoader kitLoader;

    private ArenaController arenaController;
    private DuelController duelController;

    private PlayerStatsController playerStatsController;
    private PlayerStatsRepository playerStatsRepository;

    @Override
    public void onLoad() {
        saveDefaultConfig();

        sqlDatabase = new SQLDatabase(this);

        messageProvider = new MessageProvider();

        kitController = new KitController();
        kitLoader = new KitLoader(kitController);

        arenaController = new ArenaController();
        duelController = new DuelController(messageProvider, arenaController);

        playerStatsController = new PlayerStatsController();
        playerStatsRepository = new PlayerStatsRepository(sqlDatabase, playerStatsController);
    }

    @Override
    public void onEnable() {
        sqlDatabase.init();

        playerStatsRepository.createTable();

        messageProvider.init(this);

        kitController.init(this);
        kitLoader.init(this);

        arenaController.init(this);

        getServer().getPluginManager().registerEvents(new DuelListener(arenaController,
                duelController,
                playerStatsController,
                playerStatsRepository), this);

        Objects.requireNonNull(getCommand("duel"))
                .setExecutor(new DuelCommand(messageProvider,
                        kitController,
                        arenaController,
                        duelController,
                        playerStatsController));

        getServer().getScheduler().runTaskTimer(this,
                new DuelQueue(messageProvider, arenaController, duelController),
                20L ,
                20L);
    }

    @Override
    public void onDisable() {
        sqlDatabase.shutdown();
    }

    public static DuelsPlugin getInstance() {
        return getPlugin(DuelsPlugin.class);
    }

}