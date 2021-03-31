package com.nfsprodriver.zombieland.game;

import com.nfsprodriver.zombieland.abstracts.Area;
import com.nfsprodriver.zombieland.entities.CustomZombies;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class ZombieLand {
    private final JavaPlugin plugin;
    private final Area area;
    private final BukkitScheduler scheduler;
    private final ScoreboardManager scoreboardManager;
    private final FileConfiguration config;
    private List<Player> playersInGame = Collections.emptyList();
    private List<Team> teams = Collections.emptyList();
    public Scoreboard scoreboard;
    public String name;
    public Integer timer = 0;
    public Integer level = 0;

    public ZombieLand(JavaPlugin plugin, Area area, BukkitScheduler scheduler, String name) {
        this.plugin = plugin;
        this.area = area;
        this.scheduler = scheduler;
        this.scoreboardManager = plugin.getServer().getScoreboardManager();
        this.config = plugin.getConfig();
        this.name = name;
    }

    public void init() {
        generateScoreboard();
        scheduler.runTaskTimer(plugin, () -> {
            playersInGame.clear();
            Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
            onlinePlayers.forEach(player -> {
                if (playerIsInGame(player)) {
                    playersInGame.add(player);
                    playerScoreboard(player);
                }
            });
            if (playersInGame.size() > 0) {
                timer++;
                if (timer % plugin.getConfig().getInt("zlrules.levelDuration") == 0) {
                    nextLevel();
                }
            } else {
                stopGame();
            }
        }, 20L, 20L);
    }

    private Boolean playerIsInGame(Player player) {
        Location playerLoc = player.getLocation();
        return (playerLoc.getX() > area.loc1.getX() && playerLoc.getX() < area.loc2.getX() && playerLoc.getZ() > area.loc1.getZ() && playerLoc.getZ() < area.loc2.getZ());
    }

    private Location getRandomLocation() {
        ConfigurationSection entry = config.getConfigurationSection("zlareas."+name+".entry");
        Location randomLocation = area.loc1;
        assert entry != null;
        randomLocation.setX(entry.getDouble("x"));
        randomLocation.setY(entry.getDouble("y"));
        randomLocation.setZ(entry.getDouble("z"));
        //TODO

        return randomLocation;
    }

    private void nextLevel() {
        level++;
        playersInGame.forEach(player -> {
            player.sendTitle("Level " + level, "", 20, 100, 20);
        });
        for (int i = 0; i <= level; i++) {
            Location spawnLoc = getRandomLocation();
            Zombie zombie = (Zombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
            new CustomZombies(zombie).createZombie1();
        }
    }

    private void stopGame() {
        timer = 0;
        level = 0;
        //Remove all LivingEntities in area
        double v = area.loc2.getX() - area.loc1.getX();
        double v1 = area.loc2.getY() - area.loc1.getY(); //TODO
        double v2 = area.loc2.getZ() - area.loc1.getZ();
        Collection<Entity> entities = area.loc1.getWorld().getNearbyEntities(area.loc1, v, v1, v2);
        entities.forEach(entity -> {
            if (entity instanceof Zombie) {
                entity.remove();
            }
        });
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            //player.setScoreboard(null);
        });
    }

    private void generateScoreboard() {
        scoreboard = scoreboardManager.getNewScoreboard();
        Set<String> teamNames = config.getConfigurationSection("teams").getKeys(false);
        teamNames.forEach(teamName -> {
            Team team = scoreboard.registerNewTeam(teamName);
            teams.add(team);
        });
        //TODO
    }

    private void playerScoreboard(Player player) {
        Scoreboard playerSb = player.getScoreboard();
        if (playerSb != scoreboard) {
            String teamName = (String) config.getConfigurationSection("teams").getKeys(false).toArray()[0];
            if (player.getMetadata("team").size() > 0) {
                teamName = player.getMetadata("team").get(0).asString();
            }
            String finalTeamName = teamName;
            Team playerTeam = teams.stream().filter(team -> team.getName().equals(finalTeamName)).collect(Collectors.toList()).get(0);
            playerTeam.addPlayer(player);
            player.setScoreboard(scoreboard);
        }
    }
}
