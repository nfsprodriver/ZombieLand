package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PlayerKick implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, ZombieLand> games;

    public PlayerKick(JavaPlugin plugin, Map<String, ZombieLand> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerLeave(PlayerKickEvent event) {
        Player player = event.getPlayer();
        games.values().forEach(game -> {
            game.playerLeaveGame(player);
        });
    }
}
