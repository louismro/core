package fr.louis.practice.models;

import java.util.*;

public class PlayerStatistics {
    private final UUID playerId;
    
    // Combat stats
    private int longestKillstreak;
    private int totalDamageDealt;
    private int totalDamageTaken;
    private int arrowsShot;
    private int arrowsHit;
    private int pearlsThrown;
    private int potsUsed;
    
    // Match stats
    private int matchesPlayed;
    private double averageMatchDuration;
    private int fastestWin; // seconds
    private int longestMatch; // seconds
    
    // Special
    private int perfectGames; // No damage taken
    private int comebacks; // Won from < 20% health
    private int clutches; // 1v2+ wins
    
    public PlayerStatistics(UUID playerId) {
        this.playerId = playerId;
        this.longestKillstreak = 0;
        this.totalDamageDealt = 0;
        this.totalDamageTaken = 0;
        this.arrowsShot = 0;
        this.arrowsHit = 0;
        this.pearlsThrown = 0;
        this.potsUsed = 0;
        this.matchesPlayed = 0;
        this.averageMatchDuration = 0;
        this.fastestWin = Integer.MAX_VALUE;
        this.longestMatch = 0;
        this.perfectGames = 0;
        this.comebacks = 0;
        this.clutches = 0;
    }
    
    public double getArrowAccuracy() {
        return arrowsShot == 0 ? 0 : (double) arrowsHit / arrowsShot * 100;
    }
    
    public void addMatchDuration(int seconds) {
        matchesPlayed++;
        averageMatchDuration = ((averageMatchDuration * (matchesPlayed - 1)) + seconds) / matchesPlayed;
        
        if (seconds < fastestWin) {
            fastestWin = seconds;
        }
        if (seconds > longestMatch) {
            longestMatch = seconds;
        }
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public int getLongestKillstreak() { return longestKillstreak; }
    public int getTotalDamageDealt() { return totalDamageDealt; }
    public int getTotalDamageTaken() { return totalDamageTaken; }
    public int getArrowsShot() { return arrowsShot; }
    public int getArrowsHit() { return arrowsHit; }
    public int getPearlsThrown() { return pearlsThrown; }
    public int getPotsUsed() { return potsUsed; }
    public int getMatchesPlayed() { return matchesPlayed; }
    public double getAverageMatchDuration() { return averageMatchDuration; }
    public int getFastestWin() { return fastestWin; }
    public int getLongestMatch() { return longestMatch; }
    public int getPerfectGames() { return perfectGames; }
    public int getComebacks() { return comebacks; }
    public int getClutches() { return clutches; }

    // Setters
    public void setLongestKillstreak(int longestKillstreak) { this.longestKillstreak = longestKillstreak; }
    public void setTotalDamageDealt(int totalDamageDealt) { this.totalDamageDealt = totalDamageDealt; }
    public void setTotalDamageTaken(int totalDamageTaken) { this.totalDamageTaken = totalDamageTaken; }
    public void setArrowsShot(int arrowsShot) { this.arrowsShot = arrowsShot; }
    public void setArrowsHit(int arrowsHit) { this.arrowsHit = arrowsHit; }
    public void setPearlsThrown(int pearlsThrown) { this.pearlsThrown = pearlsThrown; }
    public void setPotsUsed(int potsUsed) { this.potsUsed = potsUsed; }
    public void setMatchesPlayed(int matchesPlayed) { this.matchesPlayed = matchesPlayed; }
    public void setAverageMatchDuration(double averageMatchDuration) { this.averageMatchDuration = averageMatchDuration; }
    public void setFastestWin(int fastestWin) { this.fastestWin = fastestWin; }
    public void setLongestMatch(int longestMatch) { this.longestMatch = longestMatch; }
    public void setPerfectGames(int perfectGames) { this.perfectGames = perfectGames; }
    public void setComebacks(int comebacks) { this.comebacks = comebacks; }
    public void setClutches(int clutches) { this.clutches = clutches; }
}

