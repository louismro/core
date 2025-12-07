package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PlayerBoost {
    private final UUID playerId;
    private final BoostType type;
    private final double multiplier;
    private final LocalDateTime startTime;
    private final Duration duration;
    
    public enum BoostType {
        COINS("§6Coins", "§6⚡"),
        XP("§bXP", "§b✦"),
        KILLS("§cKills", "§c⚔"),
        ALL("§dTout", "§d★");
        
        private final String displayName;
        private final String symbol;
        
        BoostType(String displayName, String symbol) {
            this.displayName = displayName;
            this.symbol = symbol;
        }
        
        public String getDisplayName() { return displayName; }
        public String getSymbol() { return symbol; }
    }
    
    public PlayerBoost(UUID playerId, BoostType type, double multiplier, Duration duration) {
        this.playerId = playerId;
        this.type = type;
        this.multiplier = multiplier;
        this.startTime = LocalDateTime.now();
        this.duration = duration;
    }
    
    public boolean isActive() {
        return LocalDateTime.now().isBefore(startTime.plus(duration));
    }
    
    public long getRemainingSeconds() {
        LocalDateTime end = startTime.plus(duration);
        return Duration.between(LocalDateTime.now(), end).getSeconds();
    }
    
    public String getFormattedTimeRemaining() {
        long seconds = getRemainingSeconds();
        if (seconds <= 0) return "Expiré";
        
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else if (minutes > 0) {
            return minutes + "m " + secs + "s";
        } else {
            return secs + "s";
        }
    }
}
