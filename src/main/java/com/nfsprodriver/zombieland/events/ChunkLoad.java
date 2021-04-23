package com.nfsprodriver.zombieland.events;

import com.nfsprodriver.zombieland.functions.General;
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
        new General(plugin).chunkSigns(games, chunk);
    }
}
