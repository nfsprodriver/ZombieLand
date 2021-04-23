package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PlayerLeave implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, ZombieLand> games;

    public PlayerLeave(JavaPlugin plugin, Map<String, ZombieLand> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        games.values().forEach(game -> {
            if (game.playersInGame.contains(player)) {
                game.playerLeaveGame(player);
            }
        });
    }
}
