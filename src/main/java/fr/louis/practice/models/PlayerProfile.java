package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
public class PlayerProfile {
    private final UUID playerId;
    
    // Statistiques globales
    private int totalKills;
    private int totalDeaths;
    private int totalWins;
    private int totalLosses;
    private int totalMatches;
    private int bestWinStreak;
    private int currentWinStreak;
    private int bestKillStreak;
    
    // Temps de jeu
    private long totalPlaytimeMinutes;
    private LocalDateTime firstJoin;
    private LocalDateTime lastSeen;
    
    // Économie
    private int totalCoinsEarned;
    private int totalCoinsSpent;
    
    // Achievements
    private int achievementsCompleted;
    
    // Social
    private Set<UUID> friends;
    private Set<UUID> blocked;
    private int partyInvites;
    private int duelsSent;
    
    // Préférences
    private String activeTitle;
    private String customPrefix;
    private boolean showStats;
    private boolean allowDuelRequests;
    private boolean allowPartyInvites;
    private boolean showOnlineStatus;
    
    // Match records
    private int longestMatchSeconds;
    private int fastestWinSeconds;
    private int mostKillsInMatch;
    
    public PlayerProfile(UUID playerId) {
        this.playerId = playerId;
        this.friends = new HashSet<>();
        this.blocked = new HashSet<>();
        this.firstJoin = LocalDateTime.now();
        this.lastSeen = LocalDateTime.now();
        this.showStats = true;
        this.allowDuelRequests = true;
        this.allowPartyInvites = true;
        this.showOnlineStatus = true;
    }
    
    public double getKDRatio() {
        if (totalDeaths == 0) return totalKills;
        return (double) totalKills / totalDeaths;
    }
    
    public double getWinRate() {
        if (totalMatches == 0) return 0;
        return (double) totalWins / totalMatches * 100.0;
    }
    
    public void addFriend(UUID friendId) {
        friends.add(friendId);
    }
    
    public void removeFriend(UUID friendId) {
        friends.remove(friendId);
    }
    
    public boolean isFriend(UUID playerId) {
        return friends.contains(playerId);
    }
    
    public void blockPlayer(UUID playerId) {
        blocked.add(playerId);
    }
    
    public void unblockPlayer(UUID playerId) {
        blocked.remove(playerId);
    }
    
    public boolean isBlocked(UUID playerId) {
        return blocked.contains(playerId);
    }
    
    public void updateLastSeen() {
        this.lastSeen = LocalDateTime.now();
    }
    
    public void addPlaytime(long minutes) {
        this.totalPlaytimeMinutes += minutes;
    }
    
    public String getFormattedPlaytime() {
        long hours = totalPlaytimeMinutes / 60;
        long days = hours / 24;
        hours = hours % 24;
        
        if (days > 0) {
            return days + "j " + hours + "h";
        } else {
            return hours + "h " + (totalPlaytimeMinutes % 60) + "m";
        }
    }
}
