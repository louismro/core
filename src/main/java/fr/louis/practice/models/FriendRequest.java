package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class FriendRequest {
    private final UUID senderId;
    private final UUID targetId;
    private final LocalDateTime sentAt;
    private boolean accepted;
    
    public FriendRequest(UUID senderId, UUID targetId) {
        this.senderId = senderId;
        this.targetId = targetId;
        this.sentAt = LocalDateTime.now();
        this.accepted = false;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(sentAt.plusMinutes(5));
    }
}
