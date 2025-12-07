package fr.louis.practice.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Listener pour FORCER le PvP et les dégâts partout sans aucune restriction
 */
public class ForcePvPListener implements Listener {
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onPvP(EntityDamageByEntityEvent event) {
        // Si c'est du PvP, TOUJOURS autoriser
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            event.setCancelled(false);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onDamage(EntityDamageEvent event) {
        // Autoriser tous les dégâts sur les joueurs
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            // Sauf si le joueur a god mode
            if (!player.hasMetadata("god")) {
                event.setCancelled(false);
            }
        }
    }
}
