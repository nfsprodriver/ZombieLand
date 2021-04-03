package com.nfsprodriver.zombieland.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import java.util.HashMap;
import java.util.Map;

public class ZombieKilled implements Listener {
    private JavaPlugin plugin;

    public ZombieKilled(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onZombieKilled(EntityDeathEvent event) {
        NamespacedKey gameNameKey = new NamespacedKey(plugin, "gameName");
        event.getDrops().clear();
        String gameName = event.getEntity().getPersistentDataContainer().get(gameNameKey, PersistentDataType.STRING);
        assert gameName != null;
        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();
            Score score = player.getScoreboard().getObjective("zombieland" + gameName).getScore(player.getName() + " kills");
            score.setScore(score.getScore() + 1);
        }
        NamespacedKey dropsStringKey = new NamespacedKey(plugin, "customDrops");
        String dropsString = event.getEntity().getPersistentDataContainer().get(dropsStringKey, PersistentDataType.STRING);
        assert dropsString != null;
        Map<ItemStack, Float> dropsMap = stringToMap(dropsString);
        dropsMap.forEach(((itemStack, prob) -> {
            if (getRandomBoolean(prob)) {
                event.getDrops().add(itemStack);
            }
        }));
    }

    private Map<ItemStack, Float> stringToMap(String jsonString) {
        JsonArray json = new JsonParser().parse(jsonString).getAsJsonArray();
        Map<ItemStack, Float> map = new HashMap<ItemStack, Float>();
        for (int i = 0; i < json.size(); i++) {
            JsonObject item = new JsonParser().parse(json.get(i).getAsString()).getAsJsonObject();
            ItemStack itemStack = new ItemStack(Material.getMaterial(item.get("material").getAsString()), item.get("amount").getAsInt());
            Float prob = item.get("prob").getAsFloat();
            map.put(itemStack, prob);
        }

        return map;
    }

    static boolean getRandomBoolean(float probability) {
        double randomValue = Math.random();
        return randomValue <= probability;
    }
}
