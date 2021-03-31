package com.nfsprodriver.zombieland.events;


import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.logging.Logger;

public class PlayerDead implements Listener {
    private final JavaPlugin plugin;
    private Logger logger;
    private final FileConfiguration config;

    public PlayerDead(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location playerLoc = player.getLocation();
        ConfigurationSection zlareas = config.getConfigurationSection("zlareas");
        assert zlareas != null;
        Set<String> zlareasKeys = zlareas.getKeys(false);
        zlareasKeys.forEach(area -> {
            ConfigurationSection zlarea = zlareas.getConfigurationSection(area);
            assert zlarea != null;
            if ((playerLoc.getX() > (double) zlarea.get("x1")) && (playerLoc.getX() > (double) zlarea.get("x2")) && (playerLoc.getZ() > (double) zlarea.get("z1")) && (playerLoc.getZ() > (double) zlarea.get("z2")))   {
                if (player.getMetadata("zlLives" + area).size() > 0) {
                    int areaLives = player.getMetadata("zlLives" + area).get(0).asInt();
                    if (areaLives > 0) {
                        areaLives--;
                    }
                    MetadataValue newAreaLives = new FixedMetadataValue(plugin, areaLives);
                    player.setMetadata("zlLives" + area, newAreaLives);
                    /*Location loc = new General(config).goToSpawnEntry(playerLoc);
                    player.teleport(loc);*/
                }
            }
        });
    }
}
