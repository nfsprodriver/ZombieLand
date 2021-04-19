package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

public class ChunkLoad implements Listener {
    private JavaPlugin plugin;
    private Map<String, ZombieLand> games;

    public ChunkLoad(JavaPlugin plugin, Map<String, ZombieLand> games) {
        this.plugin = plugin;
        this.games = games;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        Stream<BlockState> blockStates = Arrays.stream(chunk.getTileEntities()).filter(blockState -> (blockState.getBlock().getType() == Material.OAK_SIGN || blockState.getBlock().getType() == Material.OAK_WALL_SIGN));
        blockStates.forEach(blockState -> {
            Sign sign = (Sign) blockState;
            NamespacedKey signTypeKey = new NamespacedKey(plugin, "signType");
            String type = sign.getPersistentDataContainer().get(signTypeKey, PersistentDataType.STRING);
            if (type != null && type.equals("zl")) {
                NamespacedKey zlAreaKey = new NamespacedKey(plugin, "zlArea");
                String area = sign.getPersistentDataContainer().get(zlAreaKey, PersistentDataType.STRING);
                ZombieLand game = games.get(area);
                if(!game.connectedSigns.contains(sign)) {
                    game.connectedSigns.add(sign);
                }
            }
        });
    }
}
