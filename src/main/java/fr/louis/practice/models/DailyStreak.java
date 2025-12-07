package fr.louis.practice.models;

import java.util.UUID;

public class DailyStreak {
    private final UUID playerId;
    private int currentStreak;
    private int bestStreak;
    private long lastClaimDate;
    private int totalClaims;
    
    public DailyStreak(UUID playerId) {
        this.playerId = playerId;
        this.currentStreak = 0;
        this.bestStreak = 0;
        this.lastClaimDate = 0;
        this.totalClaims = 0;
    }
    
    public boolean canClaim() {
        long now = System.currentTimeMillis();
        long daysSinceLastClaim = (now - lastClaimDate) / (24 * 60 * 60 * 1000);
        
        return daysSinceLastClaim >= 1;
    }
    
    public void claim() {
        long now = System.currentTimeMillis();
        long daysSinceLastClaim = (now - lastClaimDate) / (24 * 60 * 60 * 1000);
        
        if (daysSinceLastClaim == 1) {
            // Continue streak
            currentStreak++;
        } else if (daysSinceLastClaim > 1) {
            // Break streak
            currentStreak = 1;
        }
        
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
        
        lastClaimDate = now;
        totalClaims++;
    }
    
    public int getRewardMultiplier() {
        // Bonus based on streak
        if (currentStreak >= 30) return 5;
        if (currentStreak >= 14) return 3;
        if (currentStreak >= 7) return 2;
        return 1;
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public int getCurrentStreak() { return currentStreak; }
    public int getBestStreak() { return bestStreak; }
    public long getLastClaimDate() { return lastClaimDate; }
    public int getTotalClaims() { return totalClaims; }

    // Setters
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }
    public void setBestStreak(int bestStreak) { this.bestStreak = bestStreak; }
    public void setLastClaimDate(long lastClaimDate) { this.lastClaimDate = lastClaimDate; }
    public void setTotalClaims(int totalClaims) { this.totalClaims = totalClaims; }
}

