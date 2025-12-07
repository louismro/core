package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PlayerReport {
    private final String id;
    private final UUID reporterId;
    private final UUID reportedId;
    private final ReportReason reason;
    private final String description;
    private final LocalDateTime createdAt;
    private ReportStatus status;
    private UUID handledBy;
    private LocalDateTime handledAt;
    private String handlerNote;
    
    public enum ReportReason {
        HACKING("§cHacking/Cheat", "§c⚠"),
        TOXICITY("§6Toxicité", "§6◆"),
        SPAM("§eSpam", "§e▬"),
        INAPPROPRIATE_NAME("§dNom inapproprié", "§d✖"),
        TEAMING("§9Teaming", "§9⚑"),
        EXPLOITATION("§5Exploitation de bug", "§5⚙"),
        OTHER("§7Autre", "§7•");
        
        private final String displayName;
        private final String symbol;
        
        ReportReason(String displayName, String symbol) {
            this.displayName = displayName;
            this.symbol = symbol;
        }
        
        public String getDisplayName() { return displayName; }
        public String getSymbol() { return symbol; }
    }
    
    public enum ReportStatus {
        PENDING("§ePendant", "⏳"),
        INVESTIGATING("§6Investigation", "🔍"),
        RESOLVED("§aRésolu", "✓"),
        REJECTED("§cRejeté", "✗"),
        DUPLICATE("§7Doublon", "⚊");
        
        private final String displayName;
        private final String symbol;
        
        ReportStatus(String displayName, String symbol) {
            this.displayName = displayName;
            this.symbol = symbol;
        }
        
        public String getDisplayName() { return displayName; }
        public String getSymbol() { return symbol; }
    }
    
    public PlayerReport(UUID reporterId, UUID reportedId, ReportReason reason, String description) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.reason = reason;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.status = ReportStatus.PENDING;
    }
}
