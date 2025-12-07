package fr.louis.practice.models;

import java.util.*;

public class Queue {
    private final String name;
    private final String kitName;
    private final boolean ranked;
    private final boolean build;
    
    private String displayName;
    private String icon;
    private boolean eloEnabled;
    
    private final Map<UUID, QueueEntry> entries;
    
    public Queue(String name, String kitName, boolean ranked, boolean build) {
        this.name = name;
        this.kitName = kitName;
        this.ranked = ranked;
        this.build = build;
        this.entries = new LinkedHashMap<>();
        this.displayName = name;
        this.icon = "DIAMOND_SWORD";
        this.eloEnabled = ranked;
    }
    
    public void addPlayer(UUID player, int elo) {
        entries.put(player, new QueueEntry(player, elo, System.currentTimeMillis()));
    }
    
    public void removePlayer(UUID player) {
        entries.remove(player);
    }
    
    public boolean containsPlayer(UUID player) {
        return entries.containsKey(player);
    }
    
    public QueueEntry getEntry(UUID player) {
        return entries.get(player);
    }
    
    public int getPlayerCount() {
        return entries.size();
    }
    
    public List<QueueEntry> getEntries() {
        return new ArrayList<>(entries.values());
    }
    
    // Getters
    public String getName() { return name; }
    public String getKitName() { return kitName; }
    public boolean isRanked() { return ranked; }
    public boolean isBuild() { return build; }
    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    public boolean isEloEnabled() { return eloEnabled; }
    
    // Setters
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setIcon(String icon) { this.icon = icon; }
    public void setEloEnabled(boolean eloEnabled) { this.eloEnabled = eloEnabled; }
    
    public static class QueueEntry {
        private final UUID player;
        private final int elo;
        private final long joinTime;
        private int searchRange;
        
        public QueueEntry(UUID player, int elo, long joinTime) {
            this.player = player;
            this.elo = elo;
            this.joinTime = joinTime;
            this.searchRange = 100;
        }
        
        public void increaseRange(int amount) {
            this.searchRange += amount;
        }
        
        public long getSearchTime() {
            return (System.currentTimeMillis() - joinTime) / 1000;
        }
        
        // Getters
        public UUID getPlayer() { return player; }
        public int getElo() { return elo; }
        public long getJoinTime() { return joinTime; }
        public int getSearchRange() { return searchRange; }
        
        // Setters
        public void setSearchRange(int searchRange) { this.searchRange = searchRange; }
    }
}
