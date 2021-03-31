package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.functions.General;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SignPress implements Listener {
    private final JavaPlugin plugin;
    private Logger logger;
    private final FileConfiguration config;

    public SignPress(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onSignPress(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && clickedBlock != null && clickedBlock.getType() == Material.OAK_SIGN) {
            if (clickedBlock.getMetadata("signType").size() > 0) {
                String type = clickedBlock.getMetadata("signType").get(0).asString();
                Player player = event.getPlayer();
                Location loc = player.getLocation();
                if (type.equals("zl")) {
                    String area = clickedBlock.getMetadata("zlArea").get(0).asString();
                    if (enterGame(player, area)) {
                        loc.setX(config.getInt("zlareas." + area + ".entry.x"));
                        loc.setY(config.getInt("zlareas." + area + ".entry.y"));
                        loc.setZ(config.getInt("zlareas." + area + ".entry.z"));
                    }
                } else if(type.equals("spawn")) {
                    loc = new General(config).goToSpawnEntry(loc);
                }
                player.teleport(loc);
            }
        }
    }

    private Boolean enterGame(Player player, String area) {
        if (player.getMetadata("zlLives" + area).size() > 0) {
            return player.getMetadata("zlLives" + area).get(0).asInt() > 0;
        } else {
            MetadataValue areaLives = new FixedMetadataValue(plugin, config.getInt("zlrules.playerLives"));
            player.setMetadata("zlLives" + area, areaLives);
            return true;
        }
    }
}
