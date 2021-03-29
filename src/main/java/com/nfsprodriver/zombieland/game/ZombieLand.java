package com.nfsprodriver.zombieland.game;

import com.nfsprodriver.zombieland.abstracts.Area;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Collection;
import java.util.logging.Logger;

public class ZombieLand {
    private JavaPlugin plugin;
    private Area area;
    private BukkitScheduler scheduler;

    public ZombieLand(JavaPlugin plugin, Area area, BukkitScheduler scheduler) {
        this.plugin = plugin;
        this.area = area;
        this.scheduler = scheduler;
    }

    public void init() {

        scheduler.runTaskTimer(plugin, new Runnable() {
            @Override
            public void run() {
                //plugin.getServer().getOnlinePlayers();
            }
        }, 0, 20L);
    }
}
