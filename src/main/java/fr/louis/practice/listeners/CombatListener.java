package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class CombatListener implements Listener {
    private final PracticeCore plugin;
    
    public CombatListener(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer == null) return;
        
        // Annuler les dégâts au spawn et en file d'attente
        if (practicePlayer.getState() == PlayerState.SPAWN || 
            practicePlayer.getState() == PlayerState.QUEUE) {
            event.setCancelled(true);
            return;
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        
        Player victim = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        
        PracticePlayer victimData = plugin.getPlayerManager().getPlayer(victim);
        PracticePlayer damagerData = plugin.getPlayerManager().getPlayer(damager);
        
        if (victimData == null || damagerData == null) return;
        
        // Annuler PvP au spawn et en file d'attente
        if (victimData.getState() == PlayerState.SPAWN || victimData.getState() == PlayerState.QUEUE ||
            damagerData.getState() == PlayerState.SPAWN || damagerData.getState() == PlayerState.QUEUE) {
            event.setCancelled(true);
            return;
        }
        
        // Si en match, gérer les stats
        if (victimData.getState() == PlayerState.MATCH && 
            damagerData.getState() == PlayerState.MATCH &&
            victimData.getCurrentMatch() == damagerData.getCurrentMatch()) {
            
            Match match = victimData.getCurrentMatch();
            
            if (match != null && match.hasStarted()) {
                // Gérer le combat
                plugin.getCombatManager().tagCombat(victim, damager);
                plugin.getCombatManager().tagCombat(damager, victim);
                
                // Gérer les combos
                plugin.getCombatManager().handleCombo(damager, victim);
                
                // Mettre à jour les statistiques du match
                MatchPlayerData damagerMatchData = match.getPlayerData(damager.getUniqueId());
                MatchPlayerData victimMatchData = match.getPlayerData(victim.getUniqueId());
                
                damagerMatchData.addDamageDealt(event.getFinalDamage());
                victimMatchData.addDamageTaken(event.getFinalDamage());
            }
        }
    }
}
