package com.nfsprodriver.zombieland.game;

import com.nfsprodriver.zombieland.abstracts.Area;
import com.nfsprodriver.zombieland.entities.CustomZombie;
import com.nfsprodriver.zombieland.functions.General;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ZombieLand {
    private final JavaPlugin plugin;
    private final Integer maxLevel;
    private final Integer pauseTime;
    private final Integer playerLives;
    private final BukkitScheduler scheduler;
    private final ScoreboardManager scoreboardManager;
    private final FileConfiguration config;
    public final Area area;
    private List<Player> playersInGame = new ArrayList<>();
    public Map<Player, List<ItemStack>> savedInventories = new HashMap<>();
    public BossBar bossbar;
    public Scoreboard scoreboard;
    public String name;
    public UUID uuid;
    private Integer pauseTimer = 0;
    private Integer timer = 0;
    private Integer level = 0;
    private Integer levelTotZomb = 1;

    public ZombieLand(JavaPlugin plugin, Area area, BukkitScheduler scheduler, String name) {
        this.plugin = plugin;
        this.area = area;
        this.scheduler = scheduler;
        this.scoreboardManager = plugin.getServer().getScoreboardManager();
        this.config = plugin.getConfig();
        this.name = name;
        this.maxLevel = config.getInt("zlareas." + name + ".options.maxLevel");
        this.pauseTime = config.getInt("zlareas." + name + ".options.pauseTime");
        this.playerLives = config.getInt("zlareas." + name + ".options.playerLives");
        this.uuid = UUID.randomUUID();
    }

    public void init() {
        generateScoreboard();
        generateBossbar();
        scheduler.runTaskTimer(plugin, () -> {
            updateBossbar();
            plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (playerIsInGame(player)) {
                    if (!(playersInGame.contains(player))) {
                        playersInGame.add(player);
                        playerScoreboard(player);
                        addBossbar(player);
                    }
                } else {
                    if (playersInGame.contains(player)) {
                        playerLeaveGame(player);
                    }
                }
            });
            if (playersInGame.size() > 0) {
                timer++;
                checkPlayersInventory();
                updateActionBar();
                if (getRemainingZombies().size() == 0) {
                    pauseTimer++;
                    if (level < maxLevel) {
                        if ((pauseTimer.equals((level + 1) * 3) || pauseTimer.equals(pauseTime))) {
                            nextLevel();
                            updateActionBar();
                        }
                    } else {
                        playersInGame.forEach(player -> player.sendTitle("Congratulations!", "You won the game \"ZombieLand " + name + "\"", 20, 100, 20));
                        stopGame();
                    }
                }
            } else {
                if (timer > 0) {
                    stopGame();
                }
            }
        }, 20L, 20L);
    }

    private boolean playerIsInGame(Player player) {
        NamespacedKey areaLivesKey = new NamespacedKey(plugin, "zlLives" + name);
        Integer areaLives = player.getPersistentDataContainer().get(areaLivesKey, PersistentDataType.INTEGER);
        if (areaLives != null) {
            return areaLives != 0;
        } else {
            areaLives = playerLives;
            Location playerLoc = player.getLocation();
            if (area.locIsInArea(playerLoc)) {
                player.getPersistentDataContainer().set(areaLivesKey, PersistentDataType.INTEGER, areaLives);
                return true;
            }
        }
        return false;
    }

    private void checkPlayersInventory() {
        playersInGame.forEach(player -> {
            PlayerInventory playerInv = player.getInventory();
            boolean hasWeapon = false;
            NamespacedKey gameUuidKey = new NamespacedKey(plugin, "gameUuid");
            List<ItemStack> invStacks = savedInventories.getOrDefault(player, new ArrayList<>());
            for (ItemStack invStack : playerInv.getContents()) {
                if (invStack == null || invStack.getItemMeta() == null) {
                    continue;
                }
                String gameUuid = invStack.getItemMeta().getPersistentDataContainer().get(gameUuidKey, PersistentDataType.STRING);
                if (gameUuid == null || !(gameUuid.equals(uuid.toString()))) {
                    invStacks.add(invStack);
                    playerInv.removeItem(invStack);
                } else if (invStack.getType().name().endsWith("_SWORD") || invStack.getType().name().endsWith("_AXE")) {
                    hasWeapon = true;
                }
            }
            savedInventories.put(player, invStacks);
            if (!hasWeapon) {
                ItemStack woodenSword = new ItemStack(Material.WOODEN_SWORD, 1);
                ItemMeta swordMeta = woodenSword.getItemMeta();
                assert swordMeta != null;
                swordMeta.setDisplayName("Basic sword");
                swordMeta.getPersistentDataContainer().set(gameUuidKey, PersistentDataType.STRING, uuid.toString());
                woodenSword.setItemMeta(swordMeta);
                playerInv.addItem(woodenSword);
            }
        });
    }

    private void updateActionBar() {
        playersInGame.forEach(player -> {
            NamespacedKey areaLivesKey = new NamespacedKey(plugin, "zlLives" + name);
            Integer areaLives = player.getPersistentDataContainer().get(areaLivesKey, PersistentDataType.INTEGER);
            if (areaLives != null) {
                int hours = timer / 3600;
                int minutes = (timer % 3600) / 60;
                int seconds = timer % 60;

                String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                String actionBarText = "Lives: " + areaLives + "    Level: " + level + "    Game time: " + timeString;
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(actionBarText));
            }
        });
    }

    private Location getRandomLocation() {
        Location randomLocation = area.loc1.clone();
        Random rand = new Random();
        int x = rand.nextInt((int) area.loc2.getX() - (int) area.loc1.getX() + 1) + (int) area.loc1.getX();
        int z = rand.nextInt((int) area.loc2.getZ() - (int) area.loc1.getZ() + 1) + (int) area.loc1.getZ();
        double y = config.getDouble("zlareas." + name + ".entry.y"); //randomLocation.getWorld().getHighestBlockYAt(x, z) + 1;
        randomLocation.setX(x);
        randomLocation.setY(y);
        randomLocation.setZ(z);

        return randomLocation;
    }

    private void nextLevel() {
        pauseTimer = 0;
        level++;
        playersInGame.forEach(player -> player.sendTitle("Level " + level, "", 20, 100, 20));
        ConfigurationSection zlLevel = config.getConfigurationSection("zllevels." + level);
        assert zlLevel != null;
        Set<String> zombieTypes = zlLevel.getKeys(false);
        levelTotZomb = 0;
        zombieTypes.forEach(zombieType -> {
            int count = zlLevel.getInt(zombieType);
            levelTotZomb += count;
            for (int i = 0; i < count; i++) {
                Location spawnLoc = getRandomLocation();
                Zombie zombie = (Zombie) Objects.requireNonNull(spawnLoc.getWorld()).spawnEntity(spawnLoc, EntityType.ZOMBIE);
                CustomZombie customZombie = new CustomZombie(zombie, name, plugin);
                try {
                    customZombie.getClass().getDeclaredMethod("create" + zombieType).invoke(customZombie);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopGame() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            playerLeaveGame(player);
            NamespacedKey areaLivesKey = new NamespacedKey(plugin, "zlLives" + name);
            player.getPersistentDataContainer().remove(areaLivesKey);
        });
        uuid = UUID.randomUUID();
        pauseTimer = 0;
        timer = 0;
        level = 0;
        playersInGame.clear();
        getRemainingZombies().forEach(Entity::remove);
    }

    public void playerLeaveGame (Player player) {
        plugin.getLogger().info(player.getName() + " left the game \"Zombie Land " + name + "\"");
        removeBossbar(player);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
        player.setScoreboard(scoreboardManager.getNewScoreboard());
        Location loc1 = area.loc1.clone();
        Location spawnLoc = new General(plugin.getConfig()).goToSpawnEntry(loc1);
        player.teleport(spawnLoc);
        playersInGame.remove(player);
        player.getInventory().clear();
        giveBackInventory(player);
        NamespacedKey playerGameMoneyKey = new NamespacedKey(plugin, uuid + "_money");
        new General(config).givePlayerEmeralds(player, playerGameMoneyKey);
    }

    public void giveBackInventory(Player player) {
        List<ItemStack> invStacks = savedInventories.get(player);
        if (invStacks != null) {
            invStacks.forEach(invStack -> {
                PlayerInventory playerInv = player.getInventory();
                playerInv.addItem(invStack);
            });
            savedInventories.remove(player);
        }
    }

    private void generateScoreboard() {
        scoreboard = scoreboardManager.getNewScoreboard();
        Set<String> teamNames = Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false);
        teamNames.forEach(teamName -> scoreboard.registerNewTeam(teamName));
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
                teamName = (String) Objects.requireNonNull(config.getConfigurationSection("teams")).getKeys(false).toArray()[0];
            }
            Set<Team> teams = scoreboard.getTeams();
            String finalTeamName = teamName;
            Team playerTeam = teams.stream().filter(team -> team.getName().equals(finalTeamName)).collect(Collectors.toList()).get(0);
            playerTeam.setAllowFriendlyFire(false);
            playerTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
            playerTeam.addPlayer(player);
            playerTeam.setDisplayName(teamName);
            //Score score = Objects.requireNonNull(scoreboard.getObjective("zombieland" + name)).getScore(player.getName() + " kills");
            //score.setScore(0);
            NamespacedKey playerGameMoneyKey = new NamespacedKey(plugin, uuid + "_money");
            Integer playerGameMoney = player.getPersistentDataContainer().get(playerGameMoneyKey, PersistentDataType.INTEGER);
            if (playerGameMoney == null) {
                playerGameMoney = 0;
            }
            Score score = Objects.requireNonNull(scoreboard.getObjective("zombieland" + name)).getScore(player.getName() + " money");
            score.setScore(playerGameMoney);
            player.setScoreboard(scoreboard);
        }
    }

    private void generateBossbar() {
        bossbar = plugin.getServer().createBossBar("Remaining Zombies:", BarColor.BLUE, BarStyle.SOLID, BarFlag.PLAY_BOSS_MUSIC);
    }

    public void updateBossbar() {
        if (level == 0) {
            return;
        }
        int current = getRemainingZombies().size();
        bossbar.setTitle("Remaining Zombies: " + current);
        bossbar.setProgress((double)current / (double)levelTotZomb);
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
        return Objects.requireNonNull(area.loc1.getWorld()).getNearbyEntities(boundingBox, (entity -> Objects.equals(entity.getPersistentDataContainer().get(gameNameKey, PersistentDataType.STRING), name)));
    }
}
