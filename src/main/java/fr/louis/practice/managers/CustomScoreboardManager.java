package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Match;
import fr.louis.practice.models.PlayerSettings;
import fr.louis.practice.models.PlayerState;
import fr.louis.practice.models.PlayerStats;
import fr.louis.practice.models.PracticePlayer;
import fr.louis.practice.models.Queue; // Import explicite
import fr.louis.practice.utils.ColorUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class CustomScoreboardManager {
    private final PracticeCore plugin;
    private final Map<UUID, Scoreboard> scoreboards;
    
    public CustomScoreboardManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.scoreboards = new HashMap<>();
        
        startUpdateTask();
    }
    
    private void startUpdateTask() {
        int interval = plugin.getConfig().getInt("scoreboard.update-interval", 20);
        
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateScoreboard(player);
            }
        }, 20L, interval);
    }
    
    public void createScoreboard(Player player) {
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        
        Scoreboard scoreboard = manager.getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("practice", Criteria.DUMMY, "practice");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        String title = plugin.getConfig().getString("scoreboard.title", "<gradient:#00d4ff:#0066ff><bold>HYKO</bold></gradient>");
        objective.setDisplayName(ColorUtils.toLegacy(ColorUtils.colorize(title)));
        
        scoreboards.put(player.getUniqueId(), scoreboard);
        player.setScoreboard(scoreboard);
        
        updateScoreboard(player);
    }
    
    public void removeScoreboard(Player player) {
        scoreboards.remove(player.getUniqueId());
        org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager != null) {
            player.setScoreboard(manager.getNewScoreboard());
        }
    }
    
    public void updateScoreboard(Player player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer == null) {
            return;
        }
        
        PlayerSettings settings = practicePlayer.getSettings();
        if (settings != null && !settings.isShowScoreboard()) {
            return;
        }
        
        switch (practicePlayer.getState()) {
            case SPAWN -> updateSpawn(player);
            case QUEUE -> updateQueue(player);
            case MATCH -> updateMatch(player);
            case SPECTATING -> updateSpectating(player);
            default -> updateSpawn(player);
        }
    }
    
    public void updateSpawn(Player player) {
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            createScoreboard(player);
            return;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        List<String> lines = plugin.getConfig().getStringList("scoreboard.spawn-lines");
        
        setLines(scoreboard, replacePlaceholders(lines, player, practicePlayer));
    }
    
    public void updateQueue(Player player) {
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            createScoreboard(player);
            return;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        List<String> lines = plugin.getConfig().getStringList("scoreboard.queue-lines");
        
        setLines(scoreboard, replacePlaceholders(lines, player, practicePlayer));
    }
    
    public void updateMatch(Player player) {
        Scoreboard scoreboard = scoreboards.get(player.getUniqueId());
        if (scoreboard == null) {
            createScoreboard(player);
            return;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        List<String> lines = plugin.getConfig().getStringList("scoreboard.match-lines");
        
        setLines(scoreboard, replacePlaceholders(lines, player, practicePlayer));
    }
    
    public void updateSpectating(Player player) {
        updateSpawn(player);
    }
    
    public void updateMatch(Match match) {
        for (UUID uuid : match.getAllPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                updateMatch(player);
            }
        }
    }
    
    private void setLines(Scoreboard scoreboard, List<String> lines) {
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective == null) {
            return;
        }
        
        // Clear old scores
        for (String entry : scoreboard.getEntries()) {
            scoreboard.resetScores(entry);
        }
        
        // Set new scores
        int score = lines.size();
        for (String line : lines) {
            if (line.length() > 40) {
                line = line.substring(0, 40);
            }
            
            Score s = objective.getScore(line);
            s.setScore(score--);
        }
    }
    
    private List<String> replacePlaceholders(List<String> lines, Player player, PracticePlayer practicePlayer) {
        List<String> replaced = new ArrayList<>();
        
        for (String line : lines) {
            // Skip potions lines in queue state
            if (practicePlayer.getState() == PlayerState.QUEUE && 
                (line.contains("{your_pots}") || line.contains("{opp_pots}"))) {
                continue;
            }
            
            line = line.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()));
            line = line.replace("{fighting}", String.valueOf(plugin.getPlayerManager().getFightingCount()));
            line = line.replace("{queued}", String.valueOf(plugin.getQueueManager().getQueuedCount()));
            line = line.replace("{streak}", String.valueOf(practicePlayer.getCurrentStreak()));
            line = line.replace("{best_streak}", String.valueOf(practicePlayer.getHighestStreak()));
            line = line.replace("{coins}", String.valueOf(practicePlayer.getCoins()));
            line = line.replace("{ping}", getPing(player));
            
            // Stats globaux
            int totalWins = 0;
            int totalLosses = 0;
            int totalKills = 0;
            int totalDeaths = 0;
            for (String kit : practicePlayer.getStats().keySet()) {
                PlayerStats stats = practicePlayer.getStats(kit);
                totalWins += stats.getWins();
                totalLosses += stats.getLosses();
                totalKills += stats.getKills();
                totalDeaths += stats.getDeaths();
            }
            double kdRatio = totalDeaths > 0 ? (double) totalKills / totalDeaths : totalKills;
            line = line.replace("{total_wins}", String.valueOf(totalWins));
            line = line.replace("{total_losses}", String.valueOf(totalLosses));
            line = line.replace("{kd_ratio}", String.format("%.2f", kdRatio));
            
            // ELO placeholders
            if (line.contains("{elo_")) {
                for (String kit : practicePlayer.getElo().keySet()) {
                    line = line.replace("{elo_" + kit.toLowerCase() + "}", 
                        String.valueOf(practicePlayer.getElo(kit)));
                }
            }
            
            // Queue placeholders
            if (practicePlayer.getQueue() != null) {
                Queue queue = practicePlayer.getQueue();
                Queue.QueueEntry entry = queue.getEntry(player.getUniqueId());
                
                line = line.replace("{queue}", ChatColor.translateAlternateColorCodes('&', queue.getDisplayName()));
                line = line.replace("{time}", entry != null ? String.valueOf(entry.getSearchTime()) : "0");
                line = line.replace("{elo}", String.valueOf(practicePlayer.getElo(queue.getKitName())));
                line = line.replace("{range}", entry != null ? String.valueOf(entry.getSearchRange()) : "100");
            }
            
            // Match placeholders
            if (practicePlayer.getCurrentMatch() != null) {
                Match match = practicePlayer.getCurrentMatch();
                UUID opponent = match.getOpponent(player.getUniqueId());
                
                // Kit name
                line = line.replace("{kit}", match.getKitName());
                
                if (opponent != null) {
                    Player opp = Bukkit.getPlayer(opponent);
                    line = line.replace("{opponent}", opp != null ? opp.getName() : "Unknown");
                    line = line.replace("{opp_ping}", opp != null ? getPing(opp) : "0");
                    
                    PracticePlayer oppPractice = plugin.getPlayerManager().getPlayer(opponent);
                    if (oppPractice != null) {
                        line = line.replace("{opp_pots}", String.valueOf(countPotions(opp)));
                    }
                }
                
                line = line.replace("{duration}", formatDuration(match.getDuration()));
                line = line.replace("{combo}", String.valueOf(practicePlayer.getCombo()));
                line = line.replace("{your_ping}", getPing(player));
                line = line.replace("{your_pots}", String.valueOf(countPotions(player)));
                
                // Hits given and taken (placeholder - need implementation in Match class)
                line = line.replace("{hits_given}", "0");
                line = line.replace("{hits_taken}", "0");
            } else {
                // Default values when not in match
                line = line.replace("{kit}", "-");
                line = line.replace("{hits_given}", "0");
                line = line.replace("{hits_taken}", "0");
            }
            
            // Convertir avec ColorUtils pour supporter MiniMessage
            replaced.add(ColorUtils.toLegacy(ColorUtils.colorize(line)));
        }
        
        return replaced;
    }
    
    private String getPing(Player player) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            return String.valueOf((int) handle.getClass().getField("ping").get(handle));
        } catch (ReflectiveOperationException | IllegalArgumentException | SecurityException e) {
            return "0";
        }
    }
    
    private int countPotions(Player player) {
        int count = 0;
        for (org.bukkit.inventory.ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == org.bukkit.Material.POTION) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    private String formatDuration(long seconds) {
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format("%d:%02d", minutes, secs);
    }
}
