package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.functions.General;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
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
            Sign sign = (Sign) clickedBlock.getState();
            NamespacedKey signTypeKey = new NamespacedKey(plugin, "signType");
            String type = sign.getPersistentDataContainer().get(signTypeKey, PersistentDataType.STRING);
            assert type != null;
            Player player = event.getPlayer();
            Location loc = player.getLocation();
            if (type.equals("zl")) {
                NamespacedKey zlAreaKey = new NamespacedKey(plugin, "zlArea");
                String area = sign.getPersistentDataContainer().get(zlAreaKey, PersistentDataType.STRING);
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

    private Boolean enterGame(Player player, String area) {
        NamespacedKey zlLivesKey = new NamespacedKey(plugin, "zlLives" + area);
        Integer zlLives = player.getPersistentDataContainer().get(zlLivesKey, PersistentDataType.INTEGER);
        if (zlLives != null) {
            return zlLives > 0;
        } else {
            player.getPersistentDataContainer().set(zlLivesKey, PersistentDataType.INTEGER, config.getInt("zlareas." + area + ".options.playerLives"));
            return true;
        }
    }
}
