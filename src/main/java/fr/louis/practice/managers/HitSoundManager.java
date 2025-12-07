package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.HitSound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HitSoundManager {
    private final PracticeCore plugin;
    private final Map<UUID, String> activeSounds; // Player -> Sound ID
    private final Map<UUID, Set<String>> unlockedSounds; // Player -> Unlocked sound IDs
    private final Map<String, HitSound> availableSounds;
    
    public HitSoundManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.activeSounds = new ConcurrentHashMap<>();
        this.unlockedSounds = new ConcurrentHashMap<>();
        this.availableSounds = new HashMap<>();
        
        registerDefaultSounds();
    }
    
    private void registerDefaultSounds() {
        for (HitSound sound : HitSound.DEFAULT_SOUNDS) {
            availableSounds.put(sound.getId(), sound);
        }
    }
    
    public void playHitSound(Player player) {
        String soundId = activeSounds.get(player.getUniqueId());
        if (soundId == null) soundId = "classic";
        
        HitSound hitSound = availableSounds.get(soundId);
        if (hitSound != null) {
            var location = player.getLocation();
            if (location != null) {
                player.playSound(location, hitSound.getSound(), 1.0f, hitSound.getPitch());
            }
        }
    }
    
    public boolean purchaseSound(Player player, String soundId) {
        HitSound sound = availableSounds.get(soundId);
        if (sound == null) return false;
        
        if (hasUnlocked(player.getUniqueId(), soundId)) {
            player.sendMessage("§cVous possédez déjà ce son.");
            return false;
        }
        
        if (sound.getCoinsPrice() > 0) {
            var practicePlayer = plugin.getPlayerManager().getOrCreate(player);
            if (practicePlayer.getCoins() < sound.getCoinsPrice()) {
                player.sendMessage("§cVous n'avez pas assez de coins. Prix: §6" + sound.getCoinsPrice());
                return false;
            }
            
            practicePlayer.setCoins(practicePlayer.getCoins() - sound.getCoinsPrice());
        }
        
        unlockSound(player.getUniqueId(), soundId);
        player.sendMessage("§a✓ Son de hit débloqué: " + sound.getDisplayName());
        return true;
    }
    
    public void setActiveSound(UUID playerId, String soundId) {
        if (soundId == null) {
            activeSounds.remove(playerId);
        } else {
            activeSounds.put(playerId, soundId);
        }
    }
    
    public void unlockSound(UUID playerId, String soundId) {
        unlockedSounds.computeIfAbsent(playerId, k -> new HashSet<>()).add(soundId);
    }
    
    public boolean hasUnlocked(UUID playerId, String soundId) {
        if ("classic".equals(soundId)) return true; // Classic is free
        Set<String> unlocked = unlockedSounds.get(playerId);
        return unlocked != null && unlocked.contains(soundId);
    }
    
    public Collection<HitSound> getAllSounds() {
        return availableSounds.values();
    }
    
    public HitSound getSound(String id) {
        return availableSounds.get(id);
    }
}
