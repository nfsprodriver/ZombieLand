package com.nfsprodriver.zombieland.events;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

public class ZombieKilled implements Listener {
    private JavaPlugin plugin;

    public ZombieKilled(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onZombieKilled(EntityDeathEvent event) {
        NamespacedKey gameNameKey = new NamespacedKey(plugin, "gameName");
        String gameName = event.getEntity().getPersistentDataContainer().get(gameNameKey, PersistentDataType.STRING);
        assert gameName != null;
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            Score score = player.getScoreboard().getObjective("zombieland" + gameName).getScore(player.getName() + " kills");
            score.setScore(score.getScore() + 1);
        }
    }
}
