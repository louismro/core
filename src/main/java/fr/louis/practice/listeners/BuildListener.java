package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildListener implements Listener {
    private final PracticeCore plugin;
    
    public BuildListener(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer == null) return;
        
        // Autoriser si en mode build
        if (plugin.getBuildCommand().isInBuildMode(player)) {
            return;
        }
        
        // Interdire au spawn et en file d'attente
        if (practicePlayer.getState() == PlayerState.SPAWN || 
            practicePlayer.getState() == PlayerState.QUEUE) {
            event.setCancelled(true);
            return;
        }
        
        // En match, vérifier si le build est autorisé
        if (practicePlayer.getState() == PlayerState.MATCH) {
            Match match = practicePlayer.getCurrentMatch();
            if (match != null && !match.isBuild()) {
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer == null) return;
        
        // Autoriser si en mode build
        if (plugin.getBuildCommand().isInBuildMode(player)) {
            return;
        }
        
        // Interdire au spawn et en file d'attente
        if (practicePlayer.getState() == PlayerState.SPAWN || 
            practicePlayer.getState() == PlayerState.QUEUE) {
            event.setCancelled(true);
            return;
        }
        
        // En match, vérifier si le build est autorisé
        if (practicePlayer.getState() == PlayerState.MATCH) {
            Match match = practicePlayer.getCurrentMatch();
            if (match != null) {
                if (!match.isBuild()) {
                    event.setCancelled(true);
                } else if (!isAllowedBlock(block.getType())) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    private boolean isAllowedBlock(Material material) {
        return material == Material.COBBLESTONE ||
               material == Material.OAK_PLANKS ||
               material == Material.OAK_LOG ||
               material == Material.LADDER ||
               material == Material.COBWEB ||
               material == Material.WATER ||
               material == Material.LAVA;
    }
}
