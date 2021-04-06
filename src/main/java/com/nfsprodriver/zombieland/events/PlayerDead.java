package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class PlayerDead implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, ZombieLand> games;

    public PlayerDead(JavaPlugin plugin, Map<String, ZombieLand> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location playerLoc = player.getLocation();
        games.values().forEach(game -> {
            if (game.area.locIsInArea(playerLoc))   {
                NamespacedKey areaLivesKey = new NamespacedKey(plugin, "zlLives" + game.name);
                Integer areaLives = player.getPersistentDataContainer().get(areaLivesKey, PersistentDataType.INTEGER);
                assert areaLives != null;
                if (areaLives > 0) {
                    areaLives--;
                }
                player.getPersistentDataContainer().set(areaLivesKey, PersistentDataType.INTEGER, areaLives);
            }
        });
    }
}
