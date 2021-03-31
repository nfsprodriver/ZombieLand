package com.nfsprodriver.zombieland.commands;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

public class CreateSign implements CommandExecutor {
    private final JavaPlugin plugin;

    public CreateSign(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = ((Player) sender).getPlayer();
            assert player != null;
            Block block = player.getTargetBlockExact(10);
            if (args.length == 0) {
                return false;
            }
            String type = args[0];
            String area = "";
            if (args.length == 2) {
                area = args[1];
            }
            if (block != null && block.getType() == Material.OAK_SIGN && (type.equals("zl") || type.equals("spawn"))) {
                MetadataValue signType = new FixedMetadataValue(plugin, type);
                block.setMetadata("signType", signType);
                Sign sign = (Sign) block.getState();
                if (type.equals("zl")) {
                    MetadataValue zlArea = new FixedMetadataValue(plugin, area);
                    block.setMetadata("zlArea", zlArea);
                    sign.setLine(0, "ZombieLand " + area);
                } else if(type.equals("spawn")) {
                    sign.setLine(0, "Spawn");
                }
                sign.update();

                return true;
            } else {
                player.sendMessage("Please look at an oak sign and parse either zl or spawn as arg!");
            }
        }

        return false;
    }
}
