package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class CosmeticListener implements Listener {
    private final PracticeCore plugin;
    
    public CosmeticListener(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        
        if (projectile.getShooter() instanceof Player) {
            Player shooter = (Player) projectile.getShooter();
            plugin.getCosmeticManager().trackProjectile(projectile, shooter.getUniqueId());
        }
    }
    
    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        plugin.getCosmeticManager().untrackProjectile(event.getEntity());
    }
}
