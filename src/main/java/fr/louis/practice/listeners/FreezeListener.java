package fr.louis.practice.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FreezeListener implements Listener {
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        // Check if frozen
        if (player.hasMetadata("frozen") && player.getMetadata("frozen").get(0).asBoolean()) {
            // Cancel movement
            if (event.getFrom().getX() != event.getTo().getX() ||
                event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // If frozen and quits, ban them
        if (player.hasMetadata("frozen") && player.getMetadata("frozen").get(0).asBoolean()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), 
                "ban " + player.getName() + " Déconnexion pendant un freeze");
        }
    }
}
