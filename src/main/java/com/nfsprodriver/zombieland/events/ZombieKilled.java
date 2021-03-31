package com.nfsprodriver.zombieland.events;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

public class ZombieKilled implements Listener {
    private JavaPlugin plugin;

    public ZombieKilled(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onZombieKilled(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE && event.getEntity().getMetadata("gameName").size() > 0) {
            if (event.getEntity().getKiller() != null) {
                String gameName = event.getEntity().getMetadata("gameName").get(0).asString();
                Player player = event.getEntity().getKiller();
                Score score = player.getScoreboard().getObjective("zombieland" + gameName).getScore(player.getName() + " kills");
                score.setScore(score.getScore() + 1);
            }
        }
    }
}
