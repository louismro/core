package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
public class Warp {
    private final String name;
    private final Location location;
    private final UUID creatorId;
    private final long createdAt;
    private String displayName;
    private String permission;
    private boolean publicWarp;
    
    public Warp(String name, Location location, UUID creatorId) {
        this.name = name;
        this.location = location;
        this.creatorId = creatorId;
        this.createdAt = System.currentTimeMillis();
        this.displayName = name;
        this.permission = null;
        this.publicWarp = true;
    }
}
