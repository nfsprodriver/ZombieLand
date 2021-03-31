package com.nfsprodriver.zombieland.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class SetTeam implements CommandExecutor {
    private final JavaPlugin plugin;

    public SetTeam(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String playerName = args[0];
        String teamName = args[1];
        if (!(playerName.isEmpty()) && !(teamName.isEmpty())) {
            Player player = plugin.getServer().getPlayer(playerName);
            assert player != null;
            Set<String> teamNames = plugin.getConfig().getConfigurationSection("teams").getKeys(false);
            if ((!teamNames.contains(teamName))) {
                sender.sendMessage("Parse a valid team name!");
            }
            MetadataValue team = new FixedMetadataValue(plugin, teamName);
            player.setMetadata("team", team);

            return true;
        } else {
            sender.sendMessage("Parse a valid player and team name!");
        }

        return false;
    }
}
