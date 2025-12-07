package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Punishment {
    private final String id;
    private final UUID targetId;
    private final UUID issuerId;
    private final PunishmentType type;
    private final String reason;
    private final LocalDateTime issuedAt;
    private final long durationMinutes; // 0 = permanent
    private boolean active;
    private LocalDateTime expiresAt;
    private UUID unbannedBy;
    private LocalDateTime unbannedAt;
    
    public enum PunishmentType {
        BAN("§cBan", "⛔"),
        TEMP_BAN("§6Ban Temporaire", "⏰"),
        MUTE("§eMute", "🔇"),
        TEMP_MUTE("§eMute Temporaire", "⏲"),
        KICK("§7Kick", "⚠"),
        WARNING("§eAvertissement", "⚡");
        
        private final String displayName;
        private final String symbol;
        
        PunishmentType(String displayName, String symbol) {
            this.displayName = displayName;
            this.symbol = symbol;
        }
        
        public String getDisplayName() { return displayName; }
        public String getSymbol() { return symbol; }
    }
    
    public Punishment(UUID targetId, UUID issuerId, PunishmentType type, String reason, long durationMinutes) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.targetId = targetId;
        this.issuerId = issuerId;
        this.type = type;
        this.reason = reason;
        this.issuedAt = LocalDateTime.now();
        this.durationMinutes = durationMinutes;
        this.active = true;
        
        if (durationMinutes > 0) {
            this.expiresAt = issuedAt.plusMinutes(durationMinutes);
        }
    }
    
    public boolean isPermanent() {
        return durationMinutes == 0;
    }
    
    public boolean isExpired() {
        if (isPermanent()) return false;
        return LocalDateTime.now().isAfter(expiresAt);
    }
    
    public String getFormattedDuration() {
        if (isPermanent()) return "Permanent";
        
        long minutes = durationMinutes;
        if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "");
        }
        
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " heure" + (hours > 1 ? "s" : "");
        }
        
        long days = hours / 24;
        return days + " jour" + (days > 1 ? "s" : "");
    }
    
    public String getFormattedTimeRemaining() {
        if (isPermanent()) return "Permanent";
        if (isExpired()) return "Expiré";
        
        java.time.Duration duration = java.time.Duration.between(LocalDateTime.now(), expiresAt);
        long seconds = duration.getSeconds();
        
        if (seconds < 60) {
            return seconds + "s";
        }
        
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "m";
        }
        
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "h " + (minutes % 60) + "m";
        }
        
        long days = hours / 24;
        return days + "j " + (hours % 24) + "h";
    }
}
