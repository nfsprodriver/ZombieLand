package com.nfsprodriver.zombieland.functions;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

public class General {
    private final FileConfiguration config;

    public General(FileConfiguration config) {
        this.config = config;
    }

    public Location goToSpawnEntry(Location loc) {
        loc.setX(config.getInt("spawnentry.x"));
        loc.setY(config.getInt("spawnentry.y"));
        loc.setZ(config.getInt("spawnentry.z"));
        return loc;
    }

    public void givePlayerEmeralds(Player player, NamespacedKey dataKey) {
        Integer playerGameMoney = player.getPersistentDataContainer().get(dataKey, PersistentDataType.INTEGER);
        if (playerGameMoney == null) {
            return;
        }
        PlayerInventory playerInv = player.getInventory();
        ItemStack itemStack = new ItemStack(Material.EMERALD, (int) ((double)playerGameMoney / 10.0));
        playerInv.addItem(itemStack);
        player.getPersistentDataContainer().remove(dataKey);
    }
}
