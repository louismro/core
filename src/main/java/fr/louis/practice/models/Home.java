package fr.louis.practice.models;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
public class Home {
    private final UUID ownerId;
    private final String name;
    private final Location location;
    private final long createdAt;
    
    public Home(UUID ownerId, String name, Location location) {
        this.ownerId = ownerId;
        this.name = name;
        this.location = location;
        this.createdAt = System.currentTimeMillis();
    }
}
