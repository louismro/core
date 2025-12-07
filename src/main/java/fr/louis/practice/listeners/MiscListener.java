package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class MiscListener implements Listener {
    private final PracticeCore plugin;
    
    public MiscListener(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer == null) {
            return;
        }
        
        // Empêcher de drop au spawn ou en queue
        if (practicePlayer.getState() == PlayerState.SPAWN || 
            practicePlayer.getState() == PlayerState.QUEUE ||
            practicePlayer.getState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
            return;
        }
        
        // Vérifier si en match
        if (practicePlayer.isInMatch()) {
            Match match = practicePlayer.getCurrentMatch();
            
            // Empêcher pendant le countdown
            if (!match.hasStarted()) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer == null) {
            return;
        }
        
        // Empêcher de ramasser au spawn ou en queue
        if (practicePlayer.getState() == PlayerState.SPAWN || 
            practicePlayer.getState() == PlayerState.QUEUE ||
            practicePlayer.getState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getEntity();
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer == null) {
            event.setCancelled(true);
            return;
        }
        
        // Pas de faim au spawn ou en queue
        if (practicePlayer.getState() == PlayerState.SPAWN || 
            practicePlayer.getState() == PlayerState.QUEUE ||
            practicePlayer.getState() == PlayerState.SPECTATING) {
            event.setCancelled(true);
            return;
        }
        
        // Vérifier la configuration du kit
        if (practicePlayer.isInMatch()) {
            Match match = practicePlayer.getCurrentMatch();
            Kit kit = plugin.getKitManager().getKit(match.getKitName());
            
            if (kit != null && !kit.isHunger()) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().toLowerCase();
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer == null) {
            return;
        }
        
        // Bloquer certaines commandes en combat
        if (practicePlayer.isCombatTagged()) {
            if (command.startsWith("/spawn") || 
                command.startsWith("/lobby") ||
                command.startsWith("/hub")) {
                event.setCancelled(true);
                player.sendMessage("§cVous ne pouvez pas faire cela en combat!");
            }
        }
    }
}
