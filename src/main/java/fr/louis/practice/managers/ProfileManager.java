package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerProfile;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<UUID, PlayerProfile> profiles = new ConcurrentHashMap<>();
    
    public ProfileManager(PracticeCore plugin) {
        this.plugin = plugin;
        startPlaytimeTracker();
    }
    
    public PlayerProfile getProfile(UUID playerId) {
        return profiles.computeIfAbsent(playerId, PlayerProfile::new);
    }
    
    public PlayerProfile getOrCreateProfile(Player player) {
        return getProfile(player.getUniqueId());
    }
    
    public void updateProfile(UUID playerId) {
        PlayerProfile profile = getProfile(playerId);
        profile.updateLastSeen();
    }
    
    private void startPlaytimeTracker() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                PlayerProfile profile = getProfile(player.getUniqueId());
                profile.addPlaytime(1); // 1 minute
            }
        }, 20L * 60, 20L * 60); // Toutes les minutes
    }
    
    public void saveProfile(UUID playerId) {
        // MongoDB persistence will be implemented in a future update
    }
    
    public void loadProfile(UUID playerId) {
        // MongoDB data loading will be implemented in a future update
    }
}
