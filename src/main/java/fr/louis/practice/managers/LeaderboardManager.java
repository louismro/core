package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticePlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Getter
public class LeaderboardManager {
    private final PracticeCore plugin;
    private final Map<String, List<LeaderboardEntry>> kitLeaderboards;
    private final List<LeaderboardEntry> globalLeaderboard;
    private BukkitTask updateTask;
    
    public LeaderboardManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.kitLeaderboards = new ConcurrentHashMap<>();
        this.globalLeaderboard = new ArrayList<>();
        startUpdateTask();
    }
    
    private void startUpdateTask() {
        // Update toutes les 5 minutes
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            updateLeaderboards();
        }, 20L * 60, 20L * 60 * 5);
    }
    
    public void updateLeaderboards() {
        // Clear anciens leaderboards
        kitLeaderboards.clear();
        globalLeaderboard.clear();
        
        // Récupérer tous les joueurs
        Collection<PracticePlayer> players = plugin.getPlayerManager().getAllPlayers();
        
        // Global leaderboard (basé sur l'ELO total)
        globalLeaderboard.addAll(players.stream()
            .map(p -> new LeaderboardEntry(
                p.getUuid(),
                p.getName(),
                calculateGlobalElo(p),
                calculateTotalWins(p),
                calculateTotalLosses(p),
                calculateGlobalKD(p)
            ))
            .sorted(Comparator.comparingInt(LeaderboardEntry::getElo).reversed())
            .limit(100)
            .collect(Collectors.toList()));
        
        // Per-kit leaderboards
        for (String kitName : plugin.getKitManager().getKits().keySet()) {
            List<LeaderboardEntry> kitBoard = players.stream()
                .map(p -> new LeaderboardEntry(
                    p.getUuid(),
                    p.getName(),
                    p.getElo(kitName),
                    p.getStats(kitName).getWins(),
                    p.getStats(kitName).getLosses(),
                    p.getStats(kitName).getKDRatio()
                ))
                .sorted(Comparator.comparingInt(LeaderboardEntry::getElo).reversed())
                .limit(100)
                .collect(Collectors.toList());
            
            kitLeaderboards.put(kitName, kitBoard);
        }
    }
    
    private int calculateGlobalElo(PracticePlayer player) {
        int total = 0;
        int count = 0;
        for (int elo : player.getEloMap().values()) {
            total += elo;
            count++;
        }
        return count > 0 ? total / count : 1000;
    }
    
    private int calculateTotalWins(PracticePlayer player) {
        return player.getStatsMap().values().stream()
            .mapToInt(stats -> stats.getWins())
            .sum();
    }
    
    private int calculateTotalLosses(PracticePlayer player) {
        return player.getStatsMap().values().stream()
            .mapToInt(stats -> stats.getLosses())
            .sum();
    }
    
    private double calculateGlobalKD(PracticePlayer player) {
        int totalKills = player.getStatsMap().values().stream()
            .mapToInt(stats -> stats.getKills())
            .sum();
        int totalDeaths = player.getStatsMap().values().stream()
            .mapToInt(stats -> stats.getDeaths())
            .sum();
        return totalDeaths > 0 ? (double) totalKills / totalDeaths : totalKills;
    }
    
    public List<LeaderboardEntry> getGlobalTop(int limit) {
        return globalLeaderboard.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public List<LeaderboardEntry> getKitTop(String kitName, int limit) {
        return kitLeaderboards.getOrDefault(kitName, new ArrayList<>()).stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public int getPlayerPosition(UUID uuid, String kitName) {
        List<LeaderboardEntry> board = kitName == null ? 
            globalLeaderboard : kitLeaderboards.get(kitName);
        
        if (board == null) return -1;
        
        for (int i = 0; i < board.size(); i++) {
            if (board.get(i).getUuid().equals(uuid)) {
                return i + 1;
            }
        }
        return -1;
    }
    
    public void shutdown() {
        if (updateTask != null) {
            updateTask.cancel();
        }
    }
    
    @Getter
    public static class LeaderboardEntry {
        private final UUID uuid;
        private final String name;
        private final int elo;
        private final int wins;
        private final int losses;
        private final double kd;
        
        public LeaderboardEntry(UUID uuid, String name, int elo, int wins, int losses, double kd) {
            this.uuid = uuid;
            this.name = name;
            this.elo = elo;
            this.wins = wins;
            this.losses = losses;
            this.kd = kd;
        }

        public UUID getUuid() { return uuid; }
        public String getName() { return name; }
        public int getElo() { return elo; }
        public int getWins() { return wins; }
        public int getLosses() { return losses; }
        public double getKd() { return kd; }
    }
}
