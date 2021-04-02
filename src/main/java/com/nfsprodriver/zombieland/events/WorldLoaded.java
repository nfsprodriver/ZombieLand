package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.abstracts.Area;
import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class WorldLoaded implements Listener {
    private final JavaPlugin plugin;
    private Logger logger;
    private final FileConfiguration config;
    private Map<String, ZombieLand> games;

    public WorldLoaded(JavaPlugin plugin, Map<String, ZombieLand> games) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.config = plugin.getConfig();
        this.games = games;
    }

    @EventHandler
    public void onWorldLoaded(WorldLoadEvent event) {
        World world = event.getWorld();
        if (world.getName().equals("world")) {
            Location spawnLoc = world.getSpawnLocation();
            ConfigurationSection zlareas = config.getConfigurationSection("zlareas");
            assert zlareas != null;
            Set<String> zlareasKeys = zlareas.getKeys(false);
            zlareasKeys.forEach(zlareasKey -> {
                ConfigurationSection zlarea = zlareas.getConfigurationSection(zlareasKey);
                assert zlarea != null;
                Area area = new Area(spawnLoc, (double) zlarea.get("x1"), (double) zlarea.get("x2"), (double) zlarea.get("z1"), (double) zlarea.get("z2"));
                ZombieLand game = new ZombieLand(plugin, area, Bukkit.getScheduler(), zlareasKey);
                games.put(zlareasKey, game);
                game.init();
            });
        }
    }
}
