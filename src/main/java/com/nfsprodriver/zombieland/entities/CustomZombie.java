package com.nfsprodriver.zombieland.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

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
        zombie.setBaby(false);
        EntityEquipment equipment = zombie.getEquipment();
        assert equipment != null;
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemStack mainHand = new ItemStack(Material.IRON_SWORD);
        equipment.setHelmet(helmet);
        equipment.setItemInMainHand(mainHand);
        Map<ItemStack, Float> dropsMap = new HashMap<>(); //Float is for probability from 0.0 to 1.0
        dropsMap.put(new ItemStack(Material.BREAD, 3), 0.3F);
        NamespacedKey customDropsKey = new NamespacedKey(plugin, "customDrops");
        NamespacedKey killMoneyKey = new NamespacedKey(plugin, "killMoney");
        zombie.getPersistentDataContainer().set(customDropsKey, PersistentDataType.STRING, mapToString(dropsMap));
        zombie.getPersistentDataContainer().set(killMoneyKey, PersistentDataType.INTEGER, 10);

        return zombie;
    }

    private String mapToString(Map<ItemStack, Float> map) {
        JsonArray json = new JsonArray();
        map.forEach((itemStack, prob) -> {
            JsonObject item = new JsonObject();
            item.addProperty("material", itemStack.getType().name());
            item.addProperty("amount", itemStack.getAmount());
            item.addProperty("prob", prob);
            json.add(item.toString());
        });

        return json.toString();
    }
}
