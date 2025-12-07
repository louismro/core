package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class CombatManager {
    private final PracticeCore plugin;
    private final Map<UUID, Long> pearlCooldowns;
    private final Map<UUID, Long> combatTags;
    
    public CombatManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.pearlCooldowns = new HashMap<>();
        this.combatTags = new HashMap<>();
    }
    
    public boolean canUsePearl(Player player) {
        if (!pearlCooldowns.containsKey(player.getUniqueId())) {
            return true;
        }
        
        long cooldown = plugin.getConfig().getInt("pearl.cooldown", 16) * 1000L;
        long lastUsed = pearlCooldowns.get(player.getUniqueId());
        
        return System.currentTimeMillis() - lastUsed >= cooldown;
    }
    
    public void setPearlCooldown(Player player) {
        pearlCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
    
    public long getPearlCooldown(Player player) {
        if (!pearlCooldowns.containsKey(player.getUniqueId())) {
            return 0;
        }
        
        long cooldown = plugin.getConfig().getInt("pearl.cooldown", 16) * 1000L;
        long lastUsed = pearlCooldowns.get(player.getUniqueId());
        long remaining = cooldown - (System.currentTimeMillis() - lastUsed);
        
        return Math.max(0, remaining / 1000);
    }
    
    public void tagCombat(Player player, Player damager) {
        combatTags.put(player.getUniqueId(), System.currentTimeMillis());
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer != null) {
            practicePlayer.setCombatTagged(System.currentTimeMillis());
            practicePlayer.setLastDamager(damager.getUniqueId());
        }
        
        // Message de combat désactivé pour éviter le spam
    }
    
    public boolean isCombatTagged(Player player) {
        if (!combatTags.containsKey(player.getUniqueId())) {
            return false;
        }
        
        long duration = plugin.getConfig().getInt("combat.tag-duration", 15) * 1000L;
        long tagged = combatTags.get(player.getUniqueId());
        
        return System.currentTimeMillis() - tagged < duration;
    }
    
    public void removeCombatTag(Player player) {
        combatTags.remove(player.getUniqueId());
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer != null) {
            practicePlayer.setCombatTagged(0);
        }
    }
    
    public void handleCombo(Player attacker, Player victim) {
        PracticePlayer attackerData = plugin.getPlayerManager().getPlayer(attacker);
        PracticePlayer victimData = plugin.getPlayerManager().getPlayer(victim);
        
        if (attackerData == null || victimData == null) {
            return;
        }
        
        // Incrémenter le combo de l'attaquant
        attackerData.incrementCombo();
        
        // Réinitialiser le combo de la victime
        victimData.resetCombo();
        
        // Mettre à jour les statistiques du match
        if (attackerData.getCurrentMatch() != null) {
            Match match = attackerData.getCurrentMatch();
            MatchPlayerData data = match.getPlayerData(attacker.getUniqueId());
            data.addHit();
            data.updateCombo(attackerData.getCombo());
        }
        
        // Afficher le combo
        if (attackerData.getCombo() >= plugin.getConfig().getInt("combo.minimum-hits", 3)) {
            displayCombo(attacker, attackerData.getCombo());
        }
    }
    
    @SuppressWarnings({"all", "unused"})
    private void displayCombo(Player player, int combo) {
        // Affichage des combos complètement désactivé pour éviter le spam
    }
    
    public void handlePotionThrow(Player player, boolean hit) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer == null) {
            return;
        }
        
        if (practicePlayer.getCurrentMatch() != null) {
            Match match = practicePlayer.getCurrentMatch();
            MatchPlayerData data = match.getPlayerData(player.getUniqueId());
            data.addPotionThrown();
            
            if (!hit) {
                data.addPotionMissed();
            }
        }
    }
    
    public void handleArrowShoot(Player player, boolean hit) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer == null) {
            return;
        }
        
        if (practicePlayer.getCurrentMatch() != null) {
            Match match = practicePlayer.getCurrentMatch();
            MatchPlayerData data = match.getPlayerData(player.getUniqueId());
            
            if (hit) {
                data.addArrowHit();
            } else {
                data.addArrowShot();
            }
        }
    }
}
