package com.nfsprodriver.zombieland.events;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class PlayerDead implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public PlayerDead(JavaPlugin plugin) {
        this.plugin = plugin;
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
            if ((playerLoc.getX() > zlarea.getDouble("x1")) && (playerLoc.getX() < zlarea.getDouble("x2")) && (playerLoc.getZ() > zlarea.getDouble("z1")) && (playerLoc.getZ() < zlarea.getDouble("z2")))   {
                NamespacedKey areaLivesKey = new NamespacedKey(plugin, "zlLives" + area);
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
