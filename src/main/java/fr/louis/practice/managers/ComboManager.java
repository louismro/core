package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerCombo;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ComboManager {
    private final Map<UUID, PlayerCombo> playerCombos;
    
    public ComboManager(PracticeCore plugin) {
        this.playerCombos = new ConcurrentHashMap<>();
    }
    
    public PlayerCombo getOrCreate(UUID playerId) {
        return playerCombos.computeIfAbsent(playerId, PlayerCombo::new);
    }
    
    public void registerHit(UUID attackerId, UUID victimId) {
        PlayerCombo combo = getOrCreate(attackerId);
        
        combo.registerHit(victimId);
        
        Player attacker = Bukkit.getPlayer(attackerId);
        if (attacker != null) {
            int newCombo = combo.getCurrentCombo();
            
            // Display combo
            if (newCombo >= 2) {
                displayCombo(attacker, newCombo);
                
                // Play sound based on combo
                playComboSound(attacker, newCombo);
            }
            
            // Milestone announcements
            switch (newCombo) {
                case 5 -> attacker.sendMessage("§6§l⚔ COMBO x5!");
                case 10 -> {
                    attacker.sendMessage("§c§l⚔⚔ COMBO x10!");
                    Bukkit.broadcastMessage("§e" + attacker.getName() + " §7a un combo de §c§l10§7!");
                }
                case 20 -> {
                    attacker.sendMessage("§4§l⚔⚔⚔ COMBO x20!");
                    Bukkit.broadcastMessage("§e" + attacker.getName() + " §7a un combo de §4§lUNSTOPPABLE§7!");
                }
                default -> {
                }
            }
        }
    }
    
    public void breakCombo(UUID playerId) {
        PlayerCombo combo = playerCombos.get(playerId);
        if (combo == null) return;
        
        int finalCombo = combo.getCurrentCombo();
        combo.breakCombo();
        
        if (finalCombo >= 5) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage("§c§l✗ COMBO CASSÉ! §7(x" + finalCombo + ")");
            }
        }
    }
    
    private void displayCombo(Player player, int combo) {
        // Action bar display
        String comboText = getComboColor(combo) + "§l" + combo + " COMBO";
        
        // Send action bar (1.8 compatible)
        try {
            player.sendTitle("", comboText, 0, 10, 5);
        } catch (Exception e) {
            // Fallback to chat
            player.sendMessage(comboText);
        }
    }
    
    private String getComboColor(int combo) {
        if (combo >= 20) return "§4";
        if (combo >= 15) return "§c";
        if (combo >= 10) return "§6";
        if (combo >= 5) return "§e";
        return "§a";
    }
    
    private void playComboSound(Player player, int combo) {
        var location = player.getLocation();
        if (location == null) return;
        
        Sound sound = Sound.UI_BUTTON_CLICK;
        float pitch = 0.5f + (combo * 0.1f);
        pitch = Math.min(pitch, 2.0f);
        
        player.playSound(location, sound, 1.0f, pitch);
        
        // Special sound for milestones
        if (combo == 10) {
            player.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        } else if (combo == 20) {
            player.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.5f);
        }
    }
    
    public int getCurrentCombo(UUID playerId) {
        PlayerCombo combo = playerCombos.get(playerId);
        return combo != null ? combo.getCurrentCombo() : 0;
    }
    
    public int getBestCombo(UUID playerId) {
        PlayerCombo combo = playerCombos.get(playerId);
        return combo != null ? combo.getBestCombo() : 0;
    }
    
    public void resetCombo(UUID playerId) {
        PlayerCombo combo = playerCombos.get(playerId);
        if (combo != null) {
            combo.reset();
        }
    }
}
