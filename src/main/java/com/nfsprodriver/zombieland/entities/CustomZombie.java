package com.nfsprodriver.zombieland.entities;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CustomZombie {
    private final Zombie zombie;
    private final String gameName;
    private final JavaPlugin plugin;
    private final FileConfiguration config;

    public CustomZombie(Zombie zombie, String gameName, JavaPlugin plugin) {
        this.zombie = zombie;
        this.gameName = gameName;
        this.plugin = plugin;
        this.config = plugin.getConfig();
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

    public Zombie createCustomZombie(String name) {
        ConfigurationSection zombieConfig = config.getConfigurationSection("zombies." + name);
        NamespacedKey gameNameKey = new NamespacedKey(plugin, "gameName");
        zombie.getPersistentDataContainer().set(gameNameKey, PersistentDataType.STRING, gameName);
        if (zombieConfig == null) {
            plugin.getLogger().info("Zombie config \"" + name + "\" does not exist.");
            return zombie;
        }
        zombie.setBaby(zombieConfig.getBoolean("isBaby"));
        EntityEquipment equipment = zombie.getEquipment();
        assert equipment != null;

        if (zombieConfig.getString("helmet") != null) {
            ItemStack helmet = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(zombieConfig.getString("helmet")))));
            equipment.setHelmet(helmet);
        }
        if (zombieConfig.getString("chestplate") != null) {
            ItemStack chestplate = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(zombieConfig.getString("chestplate")))));
            equipment.setChestplate(chestplate);
        }
        if (zombieConfig.getString("leggings") != null) {
            ItemStack leggings = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(zombieConfig.getString("leggings")))));
            equipment.setLeggings(leggings);
        }
        if (zombieConfig.getString("boots") != null) {
            ItemStack boots = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(zombieConfig.getString("boots")))));
            equipment.setBoots(boots);
        }
        if (zombieConfig.getString("mainHand") != null) {
            ItemStack mainHand = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(zombieConfig.getString("mainHand")))));
            equipment.setItemInMainHand(mainHand);
        }
        if (zombieConfig.getString("offHand") != null) {
            ItemStack offHand = new ItemStack(Objects.requireNonNull(Material.getMaterial(Objects.requireNonNull(zombieConfig.getString("offHand")))));
            equipment.setItemInOffHand(offHand);
        }
        if (zombieConfig.getConfigurationSection("drops") != null) {
            ConfigurationSection drops = zombieConfig.getConfigurationSection("drops");
            Map<ItemStack, Float> dropsMap = new HashMap<>(); //Float is for probability from 0.0 to 1.0

            assert drops != null;
            drops.getKeys(false).forEach((itemName) -> {
                ConfigurationSection itemProps = drops.getConfigurationSection(itemName);
                assert itemProps != null;
                ItemStack item = new ItemStack(Objects.requireNonNull(Material.getMaterial(itemName)), itemProps.getInt("amount"));
                dropsMap.put(item, (float) itemProps.getDouble("prob"));
            });

            NamespacedKey customDropsKey = new NamespacedKey(plugin, "customDrops");
            zombie.getPersistentDataContainer().set(customDropsKey, PersistentDataType.STRING, mapToString(dropsMap));
        }
        NamespacedKey killMoneyKey = new NamespacedKey(plugin, "killMoney");
        zombie.getPersistentDataContainer().set(killMoneyKey, PersistentDataType.INTEGER, zombieConfig.getInt("money"));

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
