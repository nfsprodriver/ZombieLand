package com.nfsprodriver.zombieland;

import com.nfsprodriver.zombieland.commands.CreateSign;
import com.nfsprodriver.zombieland.commands.SetTeam;
import com.nfsprodriver.zombieland.events.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new WorldLoaded(this), this);
        getServer().getPluginManager().registerEvents(new SignPress(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDead(this), this);
        getServer().getPluginManager().registerEvents(new ZombieKilled(this), this);
        getServer().getPluginManager().registerEvents(new ZombieCombust(), this);
        getCommand("zlsign").setExecutor(new CreateSign(this));
        getCommand("zlsetteam").setExecutor(new SetTeam(this));
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
