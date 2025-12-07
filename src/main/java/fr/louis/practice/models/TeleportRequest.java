package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TeleportRequest {
    private final UUID senderId;
    private final UUID targetId;
    private final TeleportType type;
    private final long createdAt;
    
    public enum TeleportType {
        TPA, // Demandeur va vers cible
        TPAHERE // Cible vient vers demandeur
    }
    
    public TeleportRequest(UUID senderId, UUID targetId, TeleportType type) {
        this.senderId = senderId;
        this.targetId = targetId;
        this.type = type;
        this.createdAt = System.currentTimeMillis();
    }
    
    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > 60000; // 60 secondes
    }
}
