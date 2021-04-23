package com.nfsprodriver.zombieland.functions;

import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class General {
    private final JavaPlugin plugin;

    public General(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public Location goToSpawnEntry(Location loc) {
        loc.setX(plugin.getConfig().getInt("spawnentry.x"));
        loc.setY(plugin.getConfig().getInt("spawnentry.y"));
        loc.setZ(plugin.getConfig().getInt("spawnentry.z"));
        loc.setWorld(plugin.getServer().getWorld(Objects.requireNonNull(plugin.getConfig().getString("spawnentry.world"))));
        return loc;
    }

    public void givePlayerEmeralds(Player player, NamespacedKey dataKey) {
        Integer playerGameMoney = player.getPersistentDataContainer().get(dataKey, PersistentDataType.INTEGER);
        if (playerGameMoney == null) {
            return;
        }
        PlayerInventory playerInv = player.getInventory();
        ItemStack itemStack = new ItemStack(Material.EMERALD, (int) ((double)playerGameMoney / 10.0));
        Map<Integer, ItemStack> remainingItems = playerInv.addItem(itemStack);
        remainingItems.values().forEach(remainingItem -> {
            Objects.requireNonNull(player.getLocation().getWorld()).dropItemNaturally(player.getLocation(), remainingItem);
        });
        player.getPersistentDataContainer().remove(dataKey);
    }

    public void chunkSigns(Map<String, ZombieLand> games, Chunk chunk) {
        Stream<BlockState> blockStates = Arrays.stream(chunk.getTileEntities()).filter(blockState -> (blockState.getBlock().getType() == Material.OAK_SIGN || blockState.getBlock().getType() == Material.OAK_WALL_SIGN));
        blockStates.forEach(blockState -> {
            Sign sign = (Sign) blockState;
            NamespacedKey signTypeKey = new NamespacedKey(plugin, "signType");
            String type = sign.getPersistentDataContainer().get(signTypeKey, PersistentDataType.STRING);
            if (type != null && type.equals("zl")) {
                NamespacedKey zlAreaKey = new NamespacedKey(plugin, "zlArea");
                String area = sign.getPersistentDataContainer().get(zlAreaKey, PersistentDataType.STRING);
                ZombieLand game = games.get(area);
                if(game != null && !game.connectedSigns.contains(sign)) {
                    game.connectedSigns.add(sign);
                }
            }
        });
    }
}
