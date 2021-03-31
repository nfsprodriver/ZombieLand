package com.nfsprodriver.zombieland.game;

import com.nfsprodriver.zombieland.abstracts.Area;
import com.nfsprodriver.zombieland.entities.CustomZombie;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.stream.Collectors;

public class ZombieLand {
    private final JavaPlugin plugin;
    private final Area area;
    private final BukkitScheduler scheduler;
    private final ScoreboardManager scoreboardManager;
    private final FileConfiguration config;
    private List<Player> playersInGame = new ArrayList<>();
    public Scoreboard scoreboard;
    public String name;
    public Integer pauseTimer = 0;
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
                    plugin.getLogger().info(player.getName());
                    playersInGame.add(player);
                    playerScoreboard(player);
                }
            });
            if (playersInGame.size() > 0) {
                timer++;
                plugin.getLogger().info(timer.toString());
                if (getRemainingZombies().size() == 0) {
                    pauseTimer++;
                    if (pauseTimer == plugin.getConfig().getInt("zlrules.pauseTime")) {
                        nextLevel();
                    }
                }
            } else {
                stopGame();
            }
        }, 20L, 20L);
    }

    private boolean playerIsInGame(Player player) {
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
        pauseTimer = 0;
        level++;
        playersInGame.forEach(player -> {
            player.sendTitle("Level " + level, "", 20, 100, 20);
        });
        for (int i = 0; i < level; i++) {
            Location spawnLoc = getRandomLocation();
            Zombie zombie = (Zombie) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.ZOMBIE);
            new CustomZombie(zombie).createZombie1();
        }
    }

    private void stopGame() {
        timer = 0;
        level = 0;
        getRemainingZombies().forEach(Entity::remove);
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            player.setScoreboard(scoreboardManager.getNewScoreboard());
        });
    }

    private void generateScoreboard() {
        scoreboard = scoreboardManager.getNewScoreboard();
        Set<String> teamNames = config.getConfigurationSection("teams").getKeys(false);
        teamNames.forEach(teamName -> {
            scoreboard.registerNewTeam(teamName);
        });
        Objective objective = scoreboard.registerNewObjective("kills", "dummy", "Kills");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("ZombieLand");
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
            Set<Team> teams = scoreboard.getTeams();
            Team playerTeam = teams.stream().filter(team -> team.getName().equals(finalTeamName)).collect(Collectors.toList()).get(0);
            playerTeam.addPlayer(player);
            player.setScoreboard(scoreboard);
        }
    }

    private Collection<Entity> getRemainingZombies() {
        BoundingBox boundingBox = new BoundingBox(area.loc1.getX(), area.loc1.getY(), area.loc1.getZ(), area.loc2.getX(), area.loc2.getY(), area.loc2.getZ());
        return area.loc1.getWorld().getNearbyEntities(boundingBox, (entity -> entity.getType() == EntityType.ZOMBIE));
    }
}
