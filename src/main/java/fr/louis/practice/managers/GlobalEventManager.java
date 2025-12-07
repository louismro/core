package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.GlobalEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalEventManager {
    private final PracticeCore plugin;
    private final Map<String, GlobalEvent> activeEvents;
    
    public GlobalEventManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.activeEvents = new ConcurrentHashMap<>();
        
        startEventCheckTask();
    }
    
    public void startEvent(GlobalEvent event) {
        activeEvents.put(event.getId(), event);
        
        // Announce to all players
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        Bukkit.broadcastMessage("§e§l  ÉVÉNEMENT GLOBAL ACTIVÉ");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(" §6" + event.getType().getDisplayName());
        Bukkit.broadcastMessage(" §7" + event.getDescription());
        Bukkit.broadcastMessage(" §7Durée: §e" + event.getTimeRemainingFormatted());
        Bukkit.broadcastMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        Bukkit.broadcastMessage("");
        
        // Play sound
        for (Player player : Bukkit.getOnlinePlayers()) {
            var location = player.getLocation();
            if (location != null) {
                player.playSound(location, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.5f);
            }
        }
    }
    
    public void endEvent(String eventId) {
        GlobalEvent event = activeEvents.remove(eventId);
        if (event != null) {
            event.setActive(false);
            
            Bukkit.broadcastMessage("§c§l⚠ §7L'événement §e" + event.getName() + " §7est terminé!");
        }
    }
    
    public double getActiveMultiplier(GlobalEvent.EventType type) {
        return activeEvents.values().stream()
            .filter(e -> e.getType() == type && e.isActive() && !e.isExpired())
            .mapToDouble(GlobalEvent::getMultiplier)
            .sum();
    }
    
    public double getTotalXPMultiplier() {
        double multiplier = 1.0;
        for (GlobalEvent event : activeEvents.values()) {
            if (event.isActive() && !event.isExpired()) {
                if (event.getType() == GlobalEvent.EventType.DOUBLE_XP || 
                    event.getType() == GlobalEvent.EventType.TRIPLE_XP ||
                    event.getType() == GlobalEvent.EventType.HAPPY_HOUR ||
                    event.getType() == GlobalEvent.EventType.WEEKEND_BOOST) {
                    multiplier *= event.getMultiplier();
                }
            }
        }
        return multiplier;
    }
    
    public double getTotalCoinsMultiplier() {
        double multiplier = 1.0;
        for (GlobalEvent event : activeEvents.values()) {
            if (event.isActive() && !event.isExpired()) {
                if (event.getType() == GlobalEvent.EventType.DOUBLE_COINS || 
                    event.getType() == GlobalEvent.EventType.HAPPY_HOUR ||
                    event.getType() == GlobalEvent.EventType.WEEKEND_BOOST) {
                    multiplier *= event.getMultiplier();
                }
            }
        }
        return multiplier;
    }
    
    public List<GlobalEvent> getActiveEvents() {
        return new ArrayList<>(activeEvents.values());
    }
    
    private void startEventCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Check for expired events
                activeEvents.values().removeIf(event -> {
                    if (event.isExpired()) {
                        event.setActive(false);
                        Bukkit.broadcastMessage("§c§l⚠ §7L'événement §e" + event.getName() + " §7est terminé!");
                        return true;
                    }
                    return false;
                });
            }
        }.runTaskTimer(plugin, 0L, 20 * 60); // Check every minute
    }
}
