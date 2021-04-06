package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.abstracts.Area;
import com.nfsprodriver.zombieland.functions.General;
import com.nfsprodriver.zombieland.game.ZombieLand;
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

import java.util.Map;

public class SignPress implements Listener {
    private final JavaPlugin plugin;
    private final FileConfiguration config;
    private final Map<String, ZombieLand> games;

    public SignPress(JavaPlugin plugin, Map<String, ZombieLand> games) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.games = games;
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
            Location playerLoc = player.getLocation();
            if (type.equals("zl")) {
                NamespacedKey zlAreaKey = new NamespacedKey(plugin, "zlArea");
                String area = sign.getPersistentDataContainer().get(zlAreaKey, PersistentDataType.STRING);
                if (enterGame(player, area)) {
                    playerLoc.setX(config.getInt("zlareas." + area + ".entry.x"));
                    playerLoc.setY(config.getInt("zlareas." + area + ".entry.y"));
                    playerLoc.setZ(config.getInt("zlareas." + area + ".entry.z"));
                } else {
                    player.sendMessage("No lives left for running game, please wait for next game!");
                }
            } else if(type.equals("spawn")) {
                Location finalPlayerLoc = playerLoc;
                games.values().forEach(game -> {
                    if (game.area.locIsInArea(finalPlayerLoc)) {
                        game.playerLeaveGame(player);
                    }
                });
                playerLoc = new General(config).goToSpawnEntry(playerLoc);
            }
            player.teleport(playerLoc);
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
