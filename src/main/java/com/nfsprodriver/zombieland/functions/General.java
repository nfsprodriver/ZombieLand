package com.nfsprodriver.zombieland.functions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;

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
}
