package com.nfsprodriver.zombieland.game;

import com.nfsprodriver.zombieland.abstracts.Area;
import com.nfsprodriver.zombieland.entities.CustomZombie;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ZombieLand {
    private final JavaPlugin plugin;
    private final Area area;
    private final BukkitScheduler scheduler;
    private final ScoreboardManager scoreboardManager;
    private final FileConfiguration config;
    private List<Player> playersInGame = new ArrayList<>();
    public BossBar bossbar;
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
        generateBossbar();
        scheduler.runTaskTimer(plugin, () -> {
            updateBossbar();
            playersInGame.clear();
            Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
            onlinePlayers.forEach(player -> {
                if (playerIsInGame(player)) {
                    playersInGame.add(player);
                    playerScoreboard(player);
                    addBossbar(player);
                    Score score = scoreboard.getObjective("zombieland" + name).getScore("Timer");
                    score.setScore(timer);
                }
            });
            if (playersInGame.size() > 0) {
                timer++;
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
        NamespacedKey areaLivesKey = new NamespacedKey(plugin, "zlLives" + name);
        Integer areaLives = player.getPersistentDataContainer().get(areaLivesKey, PersistentDataType.INTEGER);
        if (areaLives != null) {

            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("Lives: " + areaLives));

            if (areaLives == 0) {
                return false;
            }
            return true;
        } else {
            areaLives = config.getInt("zlrules.playerLives");
            Location playerLoc = player.getLocation();
            if ((playerLoc.getX() > area.loc1.getX() && playerLoc.getX() < area.loc2.getX() && playerLoc.getZ() > area.loc1.getZ() && playerLoc.getZ() < area.loc2.getZ())) {
                player.getPersistentDataContainer().set(areaLivesKey, PersistentDataType.INTEGER, areaLives);
                return true;
            }
        }
        return false;
    }

    private Location getRandomLocation() {
        Location randomLocation = area.loc1.clone();
        Random rand = new Random();
        int x = rand.nextInt((int) area.loc2.getX() - (int) area.loc1.getX() + 1) + (int) area.loc1.getX();
        int z = rand.nextInt((int) area.loc2.getZ() - (int) area.loc1.getZ() + 1) + (int) area.loc1.getZ();
        int y = randomLocation.getWorld().getHighestBlockYAt(x, z) + 1;
        randomLocation.setX(x);
        randomLocation.setY(y);
        randomLocation.setZ(z);

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
            new CustomZombie(zombie, name, plugin).createZombie1();
        }
    }

    private void stopGame() {
        pauseTimer = 0;
        timer = 0;
        level = 0;
        getRemainingZombies().forEach(Entity::remove);
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (player.getScoreboard() == scoreboard) {
                player.setScoreboard(scoreboardManager.getNewScoreboard());
            }
            removeBossbar(player);
        });
    }

    private void generateScoreboard() {
        scoreboard = scoreboardManager.getNewScoreboard();
        Set<String> teamNames = config.getConfigurationSection("teams").getKeys(false);
        teamNames.forEach(teamName -> {
            scoreboard.registerNewTeam(teamName);
        });
        Objective objective = scoreboard.registerNewObjective("zombieland" + name, "dummy", "ZombieLand " + name);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("ZombieLand " + name);
    }

    private void playerScoreboard(Player player) {
        Scoreboard playerSb = player.getScoreboard();
        if (playerSb != scoreboard) {
            NamespacedKey teamKey = new NamespacedKey(plugin, "team");
            String teamName = player.getPersistentDataContainer().get(teamKey, PersistentDataType.STRING);
            if (teamName == null) {
                teamName = (String) config.getConfigurationSection("teams").getKeys(false).toArray()[0];;
            }
            Set<Team> teams = scoreboard.getTeams();
            String finalTeamName = teamName;
            Team playerTeam = teams.stream().filter(team -> team.getName().equals(finalTeamName)).collect(Collectors.toList()).get(0);
            playerTeam.setAllowFriendlyFire(false);
            playerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
            playerTeam.addPlayer(player);
            playerTeam.setDisplayName(teamName);
            Score score = scoreboard.getObjective("zombieland" + name).getScore("Timer");
            score.setScore(timer);
            Score score1 = scoreboard.getObjective("zombieland" + name).getScore(player.getName() + " kills");
            score1.setScore(0);
            player.setScoreboard(scoreboard);
        }
    }

    private void generateBossbar() {
        bossbar = plugin.getServer().createBossBar("ZombieLand " + name, BarColor.BLUE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
    }

    private void updateBossbar() {
        int ref = level;
        int current = getRemainingZombies().size();
        bossbar.setProgress(current / ref);
    }

    private void addBossbar(Player player) {
        if (!(bossbar.getPlayers().contains(player))) {
            bossbar.addPlayer(player);
        }
    }

    private void removeBossbar(Player player) {
        if (bossbar.getPlayers().contains(player)) {
            bossbar.removePlayer(player);
        }
    }

    private Collection<Entity> getRemainingZombies() {
        BoundingBox boundingBox = new BoundingBox(area.loc1.getX(), 0.0, area.loc1.getZ(), area.loc2.getX(), 256.0, area.loc2.getZ());
        NamespacedKey gameNameKey = new NamespacedKey(plugin, "gameName");
        return area.loc1.getWorld().getNearbyEntities(boundingBox, (entity -> entity.getPersistentDataContainer().get(gameNameKey, PersistentDataType.STRING) != null));
    }
}
