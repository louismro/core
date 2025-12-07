package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class StatisticsListener implements Listener {
    private final PracticeCore plugin;
    
    public StatisticsListener(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        
        // Record damage
        int damage = (int) event.getFinalDamage();
        plugin.getStatisticsManager().recordDamage(damager.getUniqueId(), damage);
        plugin.getStatisticsManager().recordDamageTaken(victim.getUniqueId(), damage);
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        
        Player shooter = (Player) event.getEntity().getShooter();
        
        if (event.getEntity() instanceof org.bukkit.entity.Arrow) {
            plugin.getStatisticsManager().recordArrowShot(shooter.getUniqueId());
        } else if (event.getEntity() instanceof org.bukkit.entity.EnderPearl) {
            plugin.getStatisticsManager().recordPearlThrown(shooter.getUniqueId());
        }
    }
    
    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == org.bukkit.Material.POTION) {
            plugin.getStatisticsManager().recordPotionUsed(event.getPlayer().getUniqueId());
        }
    }
}
