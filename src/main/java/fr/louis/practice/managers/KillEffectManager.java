package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.KillEffect;
import fr.louis.practice.models.KillEffect.EffectType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KillEffectManager {
    private final PracticeCore plugin;
    private final Map<UUID, String> activeEffects; // Player -> Effect ID
    private final Map<UUID, Set<String>> unlockedEffects; // Player -> Unlocked effect IDs
    private final Map<String, KillEffect> availableEffects;
    
    public KillEffectManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.activeEffects = new ConcurrentHashMap<>();
        this.unlockedEffects = new ConcurrentHashMap<>();
        this.availableEffects = new HashMap<>();
        
        registerDefaultEffects();
    }
    
    private void registerDefaultEffects() {
        // Free effects
        availableEffects.put("lightning", new KillEffect("lightning", "Éclair", EffectType.LIGHTNING, 0, false));
        availableEffects.put("hearts", new KillEffect("hearts", "Cœurs", EffectType.HEARTS, 1000, false));
        availableEffects.put("flames", new KillEffect("flames", "Flammes", EffectType.FLAMES, 1500, false));
        
        // Premium effects
        availableEffects.put("explosion", new KillEffect("explosion", "Explosion", EffectType.EXPLOSION, 3000, false));
        availableEffects.put("blood", new KillEffect("blood", "Sang", EffectType.BLOOD, 2500, false));
        availableEffects.put("magic", new KillEffect("magic", "Magie", EffectType.MAGIC, 5000, false));
        
        // VIP effects
        availableEffects.put("ender", new KillEffect("ender", "Ender", EffectType.ENDER, 0, true));
        availableEffects.put("enchant", new KillEffect("enchant", "Enchantement", EffectType.ENCHANT, 0, true));
    }
    
    public void playKillEffect(Player killer, Location victimLocation) {
        String effectId = activeEffects.get(killer.getUniqueId());
        if (effectId == null) return;
        
        KillEffect effect = availableEffects.get(effectId);
        if (effect == null) return;
        
        EffectType type = effect.getType();
        
        // Play visual effect
        var world = victimLocation.getWorld();
        if (world == null) return; // Safety check for null world
        
        for (int i = 0; i < 20; i++) {
            world.spawnParticle(type.getParticle(), victimLocation.clone().add(
                Math.random() - 0.5, 
                Math.random() * 2, 
                Math.random() - 0.5
            ), 1);
        }
        
        // Play sound
        world.playSound(victimLocation, type.getSound(), 1.0f, 1.0f);
        
        // Special effects
        switch (type) {
            case LIGHTNING -> world.strikeLightningEffect(victimLocation);
            case EXPLOSION -> world.createExplosion(victimLocation, 0.0f);
            case FLAMES, SMOKE, SLIME, HEARTS, CRITICAL, WATER, MAGIC, BLOOD, ENCHANT, ENDER -> {
                // Particle effects are already played above
            }
        }
    }
    
    public boolean purchaseEffect(Player player, String effectId) {
        KillEffect effect = availableEffects.get(effectId);
        if (effect == null) return false;
        
        if (hasUnlocked(player.getUniqueId(), effectId)) {
            player.sendMessage("§cVous possédez déjà cet effet.");
            return false;
        }
        
        if (effect.isRequiresVIP() && !player.hasPermission("practice.vip")) {
            player.sendMessage("§cCet effet nécessite le grade VIP.");
            return false;
        }
        
        if (effect.getCoinsPrice() > 0) {
            var practicePlayer = plugin.getPlayerManager().getOrCreate(player);
            if (practicePlayer.getCoins() < effect.getCoinsPrice()) {
                player.sendMessage("§cVous n'avez pas assez de coins. Prix: §6" + effect.getCoinsPrice());
                return false;
            }
            
            practicePlayer.setCoins(practicePlayer.getCoins() - effect.getCoinsPrice());
        }
        
        unlockEffect(player.getUniqueId(), effectId);
        player.sendMessage("§a✓ Effet débloqué: " + effect.getDisplayName());
        return true;
    }
    
    public void setActiveEffect(UUID playerId, String effectId) {
        if (effectId == null) {
            activeEffects.remove(playerId);
        } else {
            activeEffects.put(playerId, effectId);
        }
    }
    
    public void unlockEffect(UUID playerId, String effectId) {
        unlockedEffects.computeIfAbsent(playerId, k -> new HashSet<>()).add(effectId);
    }
    
    public boolean hasUnlocked(UUID playerId, String effectId) {
        Set<String> unlocked = unlockedEffects.get(playerId);
        return unlocked != null && unlocked.contains(effectId);
    }
    
    public List<KillEffect> getAvailableEffects(Player player) {
        List<KillEffect> effects = new ArrayList<>();
        
        for (KillEffect effect : availableEffects.values()) {
            if (!effect.isRequiresVIP() || player.hasPermission("practice.vip")) {
                effects.add(effect);
            }
        }
        
        return effects;
    }
    
    public KillEffect getEffect(String id) {
        return availableEffects.get(id);
    }
    
    public String getActiveEffect(UUID playerId) {
        return activeEffects.get(playerId);
    }
}
