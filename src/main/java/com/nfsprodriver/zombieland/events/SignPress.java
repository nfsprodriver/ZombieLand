package com.nfsprodriver.zombieland.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SignPress implements Listener {
    private Logger logger;
    private FileConfiguration config;

    public SignPress(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onLogin(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && clickedBlock != null && clickedBlock.getType() == Material.OAK_SIGN) {
            if (clickedBlock.getMetadata("signType").size() > 0) {
                String type = clickedBlock.getMetadata("signType").get(0).asString();
                Player player = event.getPlayer();
                Location loc = player.getLocation();
                if (type.equals("zl")) {
                    loc.setX(config.getInt("zlentry.x"));
                    loc.setY(config.getInt("zlentry.y"));
                    loc.setZ(config.getInt("zlentry.z"));
                } else if(type.equals("spawn")) {
                    loc.setX(config.getInt("spawnentry.x"));
                    loc.setY(config.getInt("spawnentry.y"));
                    loc.setZ(config.getInt("spawnentry.z"));
                }
                player.teleport(loc);
            }
        }
    }
}
