package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerBoost;
import fr.louis.practice.models.PlayerBoost.BoostType;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BoostManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<UUID, List<PlayerBoost>> activeBoosts = new ConcurrentHashMap<>();
    
    public BoostManager(PracticeCore plugin) {
        this.plugin = plugin;
        startExpirationTask();
    }
    
    public void addBoost(UUID playerId, BoostType type, double multiplier, Duration duration) {
        PlayerBoost boost = new PlayerBoost(playerId, type, multiplier, duration);
        activeBoosts.computeIfAbsent(playerId, k -> new ArrayList<>()).add(boost);
        
        Player player = plugin.getServer().getPlayer(playerId);
        if (player != null) {
            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "✓ Boost activé!");
            player.sendMessage(ChatColor.YELLOW + "Type: " + type.getDisplayName() + 
                ChatColor.WHITE + " x" + multiplier);
            player.sendMessage(ChatColor.YELLOW + "Durée: " + ChatColor.WHITE + 
                boost.getFormattedTimeRemaining());
            player.sendMessage("");
        }
    }
    
    public double getMultiplier(UUID playerId, BoostType type) {
        List<PlayerBoost> boosts = activeBoosts.get(playerId);
        if (boosts == null) return 1.0;
        
        double multiplier = 1.0;
        for (PlayerBoost boost : boosts) {
            if (!boost.isActive()) continue;
            
            if (boost.getType() == type || boost.getType() == BoostType.ALL) {
                multiplier *= boost.getMultiplier();
            }
        }
        
        return multiplier;
    }
    
    public List<PlayerBoost> getActiveBoosts(UUID playerId) {
        List<PlayerBoost> boosts = activeBoosts.get(playerId);
        if (boosts == null) return Collections.emptyList();
        
        List<PlayerBoost> active = new ArrayList<>();
        for (PlayerBoost boost : boosts) {
            if (boost.isActive()) {
                active.add(boost);
            }
        }
        return active;
    }
    
    public boolean hasBoost(UUID playerId, BoostType type) {
        return getMultiplier(playerId, type) > 1.0;
    }
    
    private void startExpirationTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, List<PlayerBoost>> entry : activeBoosts.entrySet()) {
                    UUID playerId = entry.getKey();
                    List<PlayerBoost> boosts = entry.getValue();
                    
                    boosts.removeIf(boost -> {
                        if (!boost.isActive()) {
                            Player player = plugin.getServer().getPlayer(playerId);
                            if (player != null) {
                                player.sendMessage(ChatColor.RED + "✗ Votre boost " + 
                                    boost.getType().getDisplayName() + 
                                    ChatColor.RED + " a expiré!");
                            }
                            return true;
                        }
                        return false;
                    });
                    
                    if (boosts.isEmpty()) {
                        activeBoosts.remove(playerId);
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 20L); // Chaque seconde
    }
    
    public int applyBoost(int baseValue, UUID playerId, BoostType type) {
        double multiplier = getMultiplier(playerId, type);
        return (int) (baseValue * multiplier);
    }
}
