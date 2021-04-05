package com.nfsprodriver.zombieland.commands;

import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Set;

public class StopGame implements CommandExecutor {
    private final Map<String, ZombieLand> games;

    public StopGame(Map<String, ZombieLand> games) {
        this.games = games;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }
        String gameName = args[0];
        ZombieLand game = games.get(gameName);
        if (game != null) {
            game.stopGame();
            return true;
        }

        return false;
    }
}
