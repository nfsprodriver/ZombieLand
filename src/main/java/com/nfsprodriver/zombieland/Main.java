package com.nfsprodriver.zombieland;

import com.nfsprodriver.zombieland.commands.CreateSign;
import com.nfsprodriver.zombieland.commands.SetTeam;
import com.nfsprodriver.zombieland.commands.StopGame;
import com.nfsprodriver.zombieland.events.*;
import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    private Map<String, ZombieLand> games = new HashMap<>();

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        //WorldLoaded MUST be first one
        getServer().getPluginManager().registerEvents(new WorldLoaded(this, games), this);
        getServer().getPluginManager().registerEvents(new ServerCommand(this, games), this);
        getServer().getPluginManager().registerEvents(new PlayerCommand(this, games), this);
        getServer().getPluginManager().registerEvents(new PlayerJoin(this, games), this);
        getServer().getPluginManager().registerEvents(new PlayerDead(this, games), this);
        getServer().getPluginManager().registerEvents(new PlayerKick(this, games), this);
        getServer().getPluginManager().registerEvents(new PlayerLeave(this, games), this);
        getServer().getPluginManager().registerEvents(new SignPress(this, games), this);
        getServer().getPluginManager().registerEvents(new ZombieKilled(this, games), this);
        getServer().getPluginManager().registerEvents(new ZombieCombust(), this);
        getServer().getPluginManager().registerEvents(new ItemDrop(), this);
        Objects.requireNonNull(getCommand("zlsign")).setExecutor(new CreateSign(this));
        Objects.requireNonNull(getCommand("zlsetteam")).setExecutor(new SetTeam(this));
        Objects.requireNonNull(getCommand("zlstopgame")).setExecutor(new StopGame(games));
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
