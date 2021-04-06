package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.functions.General;
import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;

public class PlayerJoin implements Listener {
    private final JavaPlugin plugin;
    private final Map<String, ZombieLand> games;

    public PlayerJoin(JavaPlugin plugin, Map<String, ZombieLand> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Set<NamespacedKey> dataKeys = player.getPersistentDataContainer().getKeys();
        dataKeys.forEach(dataKey -> {
            if (dataKey.getKey().endsWith("_money")) {
                String uuid = dataKey.getKey().split("_")[0];
                if (games.values().stream().noneMatch(game -> game.uuid.toString().equals(uuid))) {
                    new General(plugin.getConfig()).givePlayerEmeralds(player, dataKey);
                }
            }
        });
    }
}
