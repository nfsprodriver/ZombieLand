package com.nfsprodriver.zombieland.commands;

import com.nfsprodriver.zombieland.game.ZombieLand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

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
