package com.nfsprodriver.zombieland.entities;

import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class CustomZombies {
    private Zombie zombie;

    public CustomZombies(Zombie zombie) {
        this.zombie = zombie;
    }

    public Zombie createZombie1() {
        EntityEquipment equipment = zombie.getEquipment();
        assert equipment != null;
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack mainHand = new ItemStack(Material.IRON_SWORD);
        equipment.setHelmet(helmet);
        equipment.setItemInMainHand(mainHand);

        return zombie;
    }
}
