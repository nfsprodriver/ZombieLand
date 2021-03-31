package com.nfsprodriver.zombieland.entities;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
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
        MetadataValue zombieGameName = new FixedMetadataValue(plugin, gameName);
        zombie.setMetadata("gameName", zombieGameName);
        EntityEquipment equipment = zombie.getEquipment();
        assert equipment != null;
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack mainHand = new ItemStack(Material.IRON_SWORD);
        equipment.setHelmet(helmet);
        equipment.setItemInMainHand(mainHand);

        return zombie;
    }
}
