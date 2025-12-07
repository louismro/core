package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final PracticeCore plugin;
    
    public PlayerConnectionListener(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);
        
        // Créer ou charger le joueur
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        plugin.getMongoManager().loadPlayer(practicePlayer);
        
        // Téléporter au spawn
        player.teleport(plugin.getSpawnLocation());
        
        // Donner les items de spawn
        plugin.getInventoryManager().giveSpawnItems(player);
        
        // Créer le scoreboard
        plugin.getCustomScoreboardManager().createScoreboard(player);
        
        // Reset l'état du joueur
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setExp(0);
        player.setFireTicks(0);
        player.getActivePotionEffects().clear();
        player.setFlying(false);
        player.setAllowFlight(false);
        
        practicePlayer.setState(PlayerState.SPAWN);
        
        // Message de connexion avec grade
        String prefix = plugin.getNametagManager() != null ? 
            ChatColor.translateAlternateColorCodes('&', plugin.getNametagManager().getPrefix(player)) : "";
        Bukkit.broadcastMessage("§7[§a+§7] " + prefix + "§f" + player.getName());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer != null) {
            // Quitter la queue si en queue
            if (practicePlayer.isInQueue()) {
                plugin.getQueueManager().removeFromQueue(player);
            }
            
            // Gérer le combat tag
            if (practicePlayer.isInMatch()) {
                Match match = practicePlayer.getCurrentMatch();
                if (match != null && !match.hasEnded()) {
                    // Le joueur perd le match s'il se déconnecte
                    MatchTeam opponentTeam = match.getOpponentTeam(player.getUniqueId());
                    if (opponentTeam != null) {
                        plugin.getMatchManager().endMatch(match, opponentTeam);
                    }
                    
                    // Pénalité ELO
                    if (match.isRanked() && plugin.getConfig().getBoolean("combat.log-penalty", true)) {
                        int penalty = plugin.getConfig().getInt("combat.log-penalty-elo", 25);
                        practicePlayer.addElo(match.getKitName(), -penalty);
                    }
                }
            }
            
            // Quitter la partie
            if (practicePlayer.isInParty()) {
                Party party = practicePlayer.getParty();
                if (party != null) {
                    plugin.getPartyManager().leaveParty(player, party);
                }
            }
            
            // Sauvegarder les données
            plugin.getMongoManager().savePlayer(practicePlayer);
            
            // Supprimer le scoreboard
            plugin.getCustomScoreboardManager().removeScoreboard(player);
            
            // Retirer le joueur
            plugin.getPlayerManager().removePlayer(player.getUniqueId());
        }
        
        // Message de déconnexion avec grade
        String prefix = plugin.getNametagManager() != null ? 
            ChatColor.translateAlternateColorCodes('&', plugin.getNametagManager().getPrefix(player)) : "";
        Bukkit.broadcastMessage("§7[§c-§7] " + prefix + "§f" + player.getName());
    }
}
