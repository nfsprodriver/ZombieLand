package com.nfsprodriver.zombieland.functions;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

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
}
