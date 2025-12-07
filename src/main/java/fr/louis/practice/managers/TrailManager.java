package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Match;
import fr.louis.practice.models.PlayerTrail;
import fr.louis.practice.models.PlayerTrail.TrailType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TrailManager {
    private final PracticeCore plugin;
    private final Map<UUID, PlayerTrail> playerTrails;
    private final Map<Location, Long> temporaryBlocks; // Block -> Expire time
    private final Map<UUID, Integer> trailIndexes; // For animated trails
    
    private static final long BLOCK_DURATION = 3000; // 3 seconds
    
    public TrailManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.playerTrails = new ConcurrentHashMap<>();
        this.temporaryBlocks = new ConcurrentHashMap<>();
        this.trailIndexes = new ConcurrentHashMap<>();
        
        startTrailTask();
        startCleanupTask();
    }
    
    public PlayerTrail getOrCreate(UUID playerId) {
        return playerTrails.computeIfAbsent(playerId, PlayerTrail::new);
    }
    
    public void setTrailType(UUID playerId, TrailType type) {
        PlayerTrail trail = getOrCreate(playerId);
        trail.setType(type);
    }
    
    public void toggleTrail(UUID playerId) {
        PlayerTrail trail = getOrCreate(playerId);
        trail.setEnabled(!trail.isEnabled());
    }
    
    public void setTrailEnabled(UUID playerId, boolean enabled) {
        PlayerTrail trail = getOrCreate(playerId);
        trail.setEnabled(enabled);
    }
    
    private void startTrailTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerTrail trail = playerTrails.get(player.getUniqueId());
                    
                    if (trail == null || !trail.isEnabled()) {
                        continue;
                    }
                    
                    // Only show trail in match
                    Match match = plugin.getMatchManager().getMatchByPlayer(player);
                    if (match == null || !match.getState().equals(Match.MatchState.FIGHTING)) {
                        continue;
                    }
                    
                    // Check if player is moving
                    if (player.getVelocity().lengthSquared() < 0.01) {
                        continue;
                    }
                    
                    // Place trail block under player
                    Location loc = player.getLocation().subtract(0, 1, 0);
                    Block block = loc.getBlock();
                    
                    // Don't replace air or important blocks
                    if (block.getType() == Material.AIR || 
                        block.getType() == Material.BARRIER ||
                        block.getType() == Material.BEDROCK) {
                        continue;
                    }
                    
                    // Store original block for restoration
                    if (!temporaryBlocks.containsKey(loc)) {
                        placeTrailBlock(player, block, trail);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 2L); // Every 2 ticks (0.1s)
    }
    
    private void placeTrailBlock(Player player, Block block, PlayerTrail trail) {
        Location loc = block.getLocation();
        
        // Get trail pattern
        TrailType type = trail.getType();
        int index = trailIndexes.getOrDefault(player.getUniqueId(), 0);
        
        Material material = type.getBlocks()[index % type.getBlocks().length];
        // Note: Data values removed in 1.21 - use specific Material types instead
        
        // Save original block state
        Material originalType = block.getType();
        
        // Place trail block
        block.setType(material);
        
        // Schedule restoration
        temporaryBlocks.put(loc, System.currentTimeMillis() + BLOCK_DURATION);
        
        // Store original state for restoration
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (block.getType() == material) {
                block.setType(originalType);
                // Note: Data values removed in 1.21
            }
            temporaryBlocks.remove(loc);
        }, BLOCK_DURATION / 50); // Convert ms to ticks
        
        // Increment index for animated trails
        trailIndexes.put(player.getUniqueId(), index + 1);
    }
    
    private void startCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                temporaryBlocks.entrySet().removeIf(entry -> {
                    if (entry.getValue() < now) {
                        // Block should already be restored, just clean up map
                        return true;
                    }
                    return false;
                });
            }
        }.runTaskTimer(plugin, 0L, 100L); // Every 5 seconds
    }
    
    public List<TrailType> getAvailableTrails(Player player) {
        List<TrailType> available = new ArrayList<>();
        
        for (TrailType type : TrailType.values()) {
            // Check permissions or unlocks
            if (hasUnlockedTrail(player, type)) {
                available.add(type);
            }
        }
        
        return available;
    }
    
    private boolean hasUnlockedTrail(Player player, TrailType type) {
        // Basic trails available to everyone
        if (type == TrailType.RAINBOW || type == TrailType.FIRE || type == TrailType.ICE) {
            return true;
        }
        
        // VIP trails
        if (type == TrailType.GOLD || type == TrailType.DIAMOND) {
            return player.hasPermission("practice.trail.vip");
        }
        
        // Premium trails
        if (type == TrailType.LIGHTNING || type == TrailType.GALAXY || type == TrailType.ENCHANTED) {
            return player.hasPermission("practice.trail.premium");
        }
        
        // Default: check specific permission
        return player.hasPermission("practice.trail." + type.name().toLowerCase());
    }
    
    public void clearAllTrails() {
        for (Location loc : temporaryBlocks.keySet()) {
            Block block = loc.getBlock();
            // Restore original blocks immediately
            block.setType(Material.AIR); // Safe default
        }
        temporaryBlocks.clear();
    }
}
