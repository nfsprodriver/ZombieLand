package com.nfsprodriver.zombieland.entities;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomZombie {
    private final Zombie zombie;
    private final String gameName;
    private final JavaPlugin plugin;

    public CustomZombie(Zombie zombie, String gameName, JavaPlugin plugin) {
        this.zombie = zombie;
        this.gameName = gameName;
        this.plugin = plugin;
    }

    public Zombie createZombie1() {
        NamespacedKey gameNameKey = new NamespacedKey(plugin, "gameName");
        zombie.getPersistentDataContainer().set(gameNameKey, PersistentDataType.STRING, gameName);
        EntityEquipment equipment = zombie.getEquipment();
        assert equipment != null;
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack mainHand = new ItemStack(Material.IRON_SWORD);
        equipment.setHelmet(helmet);
        equipment.setHelmetDropChance(0.0F);
        equipment.setItemInMainHand(mainHand);
        equipment.setItemInMainHandDropChance(0.2F);

        return zombie;
    }
}
