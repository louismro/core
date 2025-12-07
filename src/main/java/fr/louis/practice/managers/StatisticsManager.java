package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerStatistics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsManager {
    private final Map<UUID, PlayerStatistics> statistics;
    
    public StatisticsManager(PracticeCore plugin) {
        this.statistics = new ConcurrentHashMap<>();
    }
    
    public PlayerStatistics getOrCreate(UUID playerId) {
        return statistics.computeIfAbsent(playerId, PlayerStatistics::new);
    }
    
    public void recordDamage(UUID attackerId, int damage) {
        PlayerStatistics stats = getOrCreate(attackerId);
        stats.setTotalDamageDealt(stats.getTotalDamageDealt() + damage);
    }
    
    public void recordDamageTaken(UUID victimId, int damage) {
        PlayerStatistics stats = getOrCreate(victimId);
        stats.setTotalDamageTaken(stats.getTotalDamageTaken() + damage);
    }
    
    public void recordArrowShot(UUID playerId) {
        PlayerStatistics stats = getOrCreate(playerId);
        stats.setArrowsShot(stats.getArrowsShot() + 1);
    }
    
    public void recordArrowHit(UUID playerId) {
        PlayerStatistics stats = getOrCreate(playerId);
        stats.setArrowsHit(stats.getArrowsHit() + 1);
    }
    
    public void recordPearlThrown(UUID playerId) {
        PlayerStatistics stats = getOrCreate(playerId);
        stats.setPearlsThrown(stats.getPearlsThrown() + 1);
    }
    
    public void recordPotionUsed(UUID playerId) {
        PlayerStatistics stats = getOrCreate(playerId);
        stats.setPotsUsed(stats.getPotsUsed() + 1);
    }
    
    public void recordMatchEnd(UUID playerId, int durationSeconds, boolean won, boolean perfect) {
        PlayerStatistics stats = getOrCreate(playerId);
        stats.addMatchDuration(durationSeconds);
        
        if (won && perfect) {
            stats.setPerfectGames(stats.getPerfectGames() + 1);
        }
    }
    
    public PlayerStatistics getStatistics(UUID playerId) {
        return statistics.get(playerId);
    }
}
