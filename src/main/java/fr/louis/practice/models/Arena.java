package fr.louis.practice.models;

import org.bukkit.Location;

import java.util.*;

public class Arena {
    private final String name;
    private Location pos1;
    private Location pos2;
    private final List<Location> spawnPoints;
    private boolean inUse;
    private String displayName;
    private String icon;
    
    public Arena(String name, Location pos1, Location pos2) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.spawnPoints = new ArrayList<>();
        this.inUse = false;
        this.displayName = name;
        this.icon = "GRASS";
    }
    
    public void addSpawnPoint(Location location) {
        spawnPoints.add(location);
    }
    
    public Location getSpawnPoint(int index) {
        if (index >= 0 && index < spawnPoints.size()) {
            return spawnPoints.get(index).clone();
        }
        return null;
    }
    
    public boolean isLocationInArena(Location location) {
        if (location.getWorld() != pos1.getWorld()) return false;
        
        double minX = Math.min(pos1.getX(), pos2.getX());
        double maxX = Math.max(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());
        
        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }
    
    public Location getCenter() {
        double x = (pos1.getX() + pos2.getX()) / 2;
        double y = (pos1.getY() + pos2.getY()) / 2;
        double z = (pos1.getZ() + pos2.getZ()) / 2;
        return new Location(pos1.getWorld(), x, y, z);
    }
    
    // Getters
    public String getName() { return name; }
    public Location getPos1() { return pos1; }
    public Location getPos2() { return pos2; }
    public List<Location> getSpawnPoints() { return spawnPoints; }
    public List<Location> getSpawns() { return spawnPoints; } // Alias
    public boolean isInUse() { return inUse; }
    public String getDisplayName() { return displayName; }
    public String getIcon() { return icon; }
    
    // Setters
    public void setPos1(Location pos1) { this.pos1 = pos1; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }
    public void addSpawn(Location location) { spawnPoints.add(location); }
    public void setInUse(boolean inUse) { this.inUse = inUse; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public void setIcon(String icon) { this.icon = icon; }
}
