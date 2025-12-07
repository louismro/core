package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Match;
import fr.louis.practice.models.MatchTeam;
import fr.louis.practice.models.PlayerState;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.UUID;

public class DeathListener implements Listener {
    private final PracticeCore plugin;
    
    public DeathListener(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        
        event.setDeathMessage(null);
        event.getDrops().clear();
        event.setDroppedExp(0);
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer == null) {
            return;
        }
        
        // Vérifier si en match
        if (!practicePlayer.isInMatch()) {
            return;
        }
        
        Match match = practicePlayer.getCurrentMatch();
        if (match == null || match.hasEnded()) {
            return;
        }
        
        MatchTeam playerTeam = match.getTeam(player.getUniqueId());
        if (playerTeam != null) {
            playerTeam.killPlayer(player.getUniqueId());
        }
        
        // Mettre à jour les statistiques
        practicePlayer.getStats(match.getKitName()).addDeath();
        practicePlayer.setCurrentStreak(0);
        
        if (killer != null && killer instanceof Player) {
            PracticePlayer killerData = plugin.getPlayerManager().getPlayer(killer);
            if (killerData != null) {
                killerData.getStats(match.getKitName()).addKill();
                killerData.setCurrentStreak(killerData.getCurrentStreak() + 1);
                killerData.getStats(match.getKitName()).updateStreak(killerData.getCurrentStreak());
                
                // Annonce de killstreak
                plugin.getKillstreakManager().handleKill(killer);
                
                // ★ KILL EFFECT ★
                plugin.getCosmeticManager().playKillEffect(player.getLocation(), killer.getUniqueId());
                
                // ★ ACHIEVEMENT CHECKS ★
                int totalKills = killerData.getStats(match.getKitName()).getKills();
                int currentStreak = killerData.getCurrentStreak();
                plugin.getAchievementManager().checkKillAchievements(killer, totalKills);
                plugin.getAchievementManager().checkStreakAchievements(killer, currentStreak);
                
                // Message de mort
                if (plugin.getConfig().getBoolean("messages.death-messages", true)) {
                    String deathMsg = "§e" + killer.getName() + " §7a tué §e" + player.getName();
                    
                    for (UUID uuid : match.getAllPlayers()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) {
                            p.sendMessage(deathMsg);
                        }
                    }
                    
                    for (UUID uuid : match.getSpectators()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) {
                            p.sendMessage(deathMsg);
                        }
                    }
                }
            }
        }
        
        // ★ DEATH EFFECT ★
        plugin.getCosmeticManager().playDeathEffect(player.getLocation(), player.getUniqueId());
        
        // Vérifier si le match est terminé
        checkMatchEnd(match);
        
        // Réinitialiser l'état de combat
        practicePlayer.resetCombat();
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer == null) {
            event.setRespawnLocation(plugin.getSpawnLocation());
            Bukkit.getScheduler().runTask(plugin, () -> {
                player.spigot().respawn();
            });
            return;
        }
        
        // Téléporter au spawn
        event.setRespawnLocation(plugin.getSpawnLocation());
        
        // Respawn immédiat et forcé
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.spigot().respawn();
            
            // Réinitialisation immédiate
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            
            // Téléporter au spawn
            player.teleport(plugin.getSpawnLocation());
            
            practicePlayer.setState(PlayerState.SPAWN);
            practicePlayer.setCurrentMatch(null);
            
            plugin.getInventoryManager().giveSpawnItems(player);
            plugin.getCustomScoreboardManager().updateSpawn(player);
        });
    }
    
    private void checkMatchEnd(Match match) {
        if (match.hasEnded()) {
            return;
        }
        
        // Compter les équipes encore en vie
        int aliveTeams = 0;
        MatchTeam winnerTeam = null;
        
        for (MatchTeam team : match.getTeams()) {
            if (!team.isEliminated()) {
                aliveTeams++;
                winnerTeam = team;
            }
        }
        
        // Si une seule équipe reste, elle gagne
        if (aliveTeams == 1 && winnerTeam != null) {
            plugin.getMatchManager().endMatch(match, winnerTeam);
        } else if (aliveTeams == 0) {
            // Match nul (ne devrait pas arriver)
            plugin.getMatchManager().endMatch(match, null);
        }
    }
}
