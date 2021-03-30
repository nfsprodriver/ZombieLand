package com.nfsprodriver.zombieland;

import com.nfsprodriver.zombieland.abstracts.Area;
import com.nfsprodriver.zombieland.commands.CreateSign;
import com.nfsprodriver.zombieland.events.SignPress;
import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(new SignPress(this), this);
        getCommand("zlsign").setExecutor(new CreateSign(this));

        Location spawnLoc = getServer().getWorld("world").getSpawnLocation();
        List<Map<?, ?>> zlareas = getConfig().getMapList("zlareas");
        zlareas.forEach((zlarea) -> {
            Area area = new Area(spawnLoc, (double) zlarea.get("x1"), (double) zlarea.get("x2"), (double) zlarea.get("z1"), (double) zlarea.get("z2"));
            ZombieLand game = new ZombieLand(this, area, Bukkit.getScheduler());
            game.init();
        });
        getLogger().info("ZombieLand enabled!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getLogger().info("ZombieLand disabled!");
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
    }

    @Override
    public void saveConfig() {
        super.saveConfig();
    }
}
