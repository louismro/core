package fr.louis.practice.models;

import java.util.*;

public class Leaderboard {
    private final LeaderboardType type;
    private final String displayName;
    private List<LeaderboardEntry> entries;
    private long lastUpdate;
    
    public enum LeaderboardType {
        GLOBAL_ELO("§6ELO Global"),
        WINS("§aVictoires"),
        KILLSTREAK("§cKillstreak"),
        COINS("§6Coins"),
        LEVEL("§bNiveau");
        
        private final String displayName;
        
        LeaderboardType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
        public static class LeaderboardEntry {
        private final int rank;
        private final UUID playerId;
        private final String playerName;
        private final int value;

        public LeaderboardEntry(int rank, UUID playerId, String playerName, int value) {
            this.rank = rank;
            this.playerId = playerId;
            this.playerName = playerName;
            this.value = value;
        }
        
        public String getRankSymbol() {
            switch (rank) {
                case 1: return "§6★";
                case 2: return "§7★";
                case 3: return "§c★";
                default: return "§8▪";
            }
        }

        // Getters
        public int getRank() { return rank; }
        public UUID getPlayerId() { return playerId; }
        public String getPlayerName() { return playerName; }
        public int getValue() { return value; }
    }
    
    public Leaderboard(LeaderboardType type, String displayName) {
        this.type = type;
        this.displayName = displayName;
        this.entries = new ArrayList<>();
        this.lastUpdate = System.currentTimeMillis();
    }
    
    public void updateEntries(List<LeaderboardEntry> newEntries) {
        this.entries = newEntries;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    public boolean needsUpdate() {
        // Update every 5 minutes
        return System.currentTimeMillis() - lastUpdate > 5 * 60 * 1000;
    }

    // Getters
    public LeaderboardType getType() { return type; }
    public String getDisplayName() { return displayName; }
    public List<LeaderboardEntry> getEntries() { return entries; }
    public long getLastUpdate() { return lastUpdate; }

    // Setters
    public void setEntries(List<LeaderboardEntry> entries) { this.entries = entries; }
    public void setLastUpdate(long lastUpdate) { this.lastUpdate = lastUpdate; }
}

