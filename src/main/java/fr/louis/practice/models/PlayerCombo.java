package fr.louis.practice.models;

import java.util.UUID;

public class PlayerCombo {
    private final UUID playerId;
    private int currentCombo;
    private int bestCombo;
    private UUID lastHitPlayer;
    private long lastHitTime;
    
    private static final long COMBO_TIMEOUT = 3000; // 3 seconds
    
    public PlayerCombo(UUID playerId) {
        this.playerId = playerId;
        this.currentCombo = 0;
        this.bestCombo = 0;
        this.lastHitTime = 0;
    }
    
    public void registerHit(UUID targetId) {
        long now = System.currentTimeMillis();
        
        // Check if combo continues (same target, within timeout)
        if (targetId.equals(lastHitPlayer) && (now - lastHitTime) <= COMBO_TIMEOUT) {
            currentCombo++;
        } else {
            // New combo
            currentCombo = 1;
        }
        
        lastHitPlayer = targetId;
        lastHitTime = now;
        
        if (currentCombo > bestCombo) {
            bestCombo = currentCombo;
        }
    }
    
    public void breakCombo() {
        currentCombo = 0;
        lastHitPlayer = null;
    }
    
    public boolean isComboActive() {
        return currentCombo > 0 && 
               (System.currentTimeMillis() - lastHitTime) <= COMBO_TIMEOUT;
    }
    
    public void reset() {
        currentCombo = 0;
        lastHitPlayer = null;
        lastHitTime = 0;
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public int getCurrentCombo() { return currentCombo; }
    public int getBestCombo() { return bestCombo; }
    public UUID getLastHitPlayer() { return lastHitPlayer; }
    public long getLastHitTime() { return lastHitTime; }

    // Setters
    public void setCurrentCombo(int currentCombo) { this.currentCombo = currentCombo; }
    public void setBestCombo(int bestCombo) { this.bestCombo = bestCombo; }
    public void setLastHitPlayer(UUID lastHitPlayer) { this.lastHitPlayer = lastHitPlayer; }
    public void setLastHitTime(long lastHitTime) { this.lastHitTime = lastHitTime; }
}

