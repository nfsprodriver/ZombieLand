package com.nfsprodriver.zombieland.game;

import com.nfsprodriver.zombieland.abstracts.Area;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ZombieLand {
    private final JavaPlugin plugin;
    private final Area area;
    private final BukkitScheduler scheduler;
    private final ScoreboardManager scoreboardManager;
    private List<Player> playersInGame = Collections.emptyList();
    public List<Scoreboard> scoreboardList;
    public Integer timer = 0;
    public Integer level = 0;

    public ZombieLand(JavaPlugin plugin, Area area, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.area = area;
        this.scheduler = scheduler;
        this.scoreboardManager = plugin.getServer().getScoreboardManager();
    }

    public void init() {

        scheduler.runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                playersInGame.clear();
                Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
                onlinePlayers.forEach(player -> {
                    if (playerIsInGame(player)) {
                        playersInGame.add(player);
                    }
                });
                if (playersInGame.size() > 0) {
                    timer++;
                    if (timer % plugin.getConfig().getInt("zlrules.levelDuration") == 0) {
                        level++;
                    }
                } else {
                    timer = 0;
                    level = 0;
                    //Remove all LivingEntities in area
                }
            }
        }, 20L, 20L);
    }

    private Boolean playerIsInGame(Player player) {
        Location playerLoc = player.getLocation();
        return (playerLoc.getX() > area.loc1.getX() && playerLoc.getX() < area.loc2.getX() && playerLoc.getZ() > area.loc1.getZ() && playerLoc.getZ() < area.loc2.getZ());
    }
}
