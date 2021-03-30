package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.abstracts.Area;
import com.nfsprodriver.zombieland.functions.General;
import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class PlayerDead implements Listener {
    private JavaPlugin plugin;
    private Logger logger;
    private FileConfiguration config;

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
                    Location loc = new General(config).goToSpawnEntry(playerLoc);
                    player.teleport(loc);
                }
            }
        });
    }
}
