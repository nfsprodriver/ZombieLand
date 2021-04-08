package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class ServerCommand implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, ZombieLand> games;

    public ServerCommand(JavaPlugin plugin, Map<String, ZombieLand> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        if (command.equals("stop") || command.equals("restart")) {
            games.values().forEach(game -> {
                game.savedInventories.keySet().forEach(game::giveBackInventory);
            });
        }
    }
}
