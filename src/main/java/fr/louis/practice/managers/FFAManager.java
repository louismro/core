package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.FFAEvent;
import fr.louis.practice.models.Kit;
import fr.louis.practice.models.PracticePlayer;
import fr.louis.practice.models.PlayerState;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class FFAManager {
    private final PracticeCore plugin;
    private final Map<UUID, FFAEvent> activeEvents;
    private final Map<UUID, UUID> playerToEvent;
    private BukkitTask scoreboardTask;
    
    public FFAManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.activeEvents = new ConcurrentHashMap<>();
        this.playerToEvent = new ConcurrentHashMap<>();
    }
    
    public FFAEvent createFFA(String kitName, Location spawn, int maxPlayers) {
        if (plugin.getKitManager().getKit(kitName) == null) {
            return null;
        }
        
        FFAEvent event = new FFAEvent(kitName, spawn, maxPlayers);
        activeEvents.put(event.getEventId(), event);
        
        broadcastMessage(ChatColor.GOLD + "═══════════════════════════════");
        broadcastMessage(ChatColor.YELLOW + "⚔ FFA " + kitName + " créé!");
        broadcastMessage(ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + "0/" + maxPlayers);
        broadcastMessage(ChatColor.GREEN + "» /ffa join pour participer");
        broadcastMessage(ChatColor.GOLD + "═══════════════════════════════");
        
        return event;
    }
    
    public boolean joinFFA(Player player, UUID eventId) {
        FFAEvent event = activeEvents.get(eventId);
        if (event == null) return false;
        
        if (event.isFull()) {
            player.sendMessage(ChatColor.RED + "L'event FFA est plein!");
            return false;
        }
        
        if (event.getState() != FFAEvent.FFAState.WAITING && 
            event.getState() != FFAEvent.FFAState.ACTIVE) {
            player.sendMessage(ChatColor.RED + "Cet event FFA n'accepte plus de participants!");
            return false;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (practicePlayer.getState() != PlayerState.SPAWN) {
            player.sendMessage(ChatColor.RED + "Vous devez être au spawn!");
            return false;
        }
        
        // Add to event
        event.addParticipant(player.getUniqueId());
        playerToEvent.put(player.getUniqueId(), eventId);
        
        // Teleport and give kit
        player.teleport(event.getSpawnLocation());
        Kit kit = plugin.getKitManager().getKit(event.getKit());
        if (kit != null) {
            player.getInventory().setContents(kit.getContents());
            player.getInventory().setArmorContents(kit.getArmor());
        }
        
        practicePlayer.setState(PlayerState.FFA);
        
        player.sendMessage(ChatColor.GREEN + "Vous avez rejoint le FFA " + event.getKit() + "!");
        broadcastEvent(event, ChatColor.YELLOW + player.getName() + ChatColor.GRAY + 
            " a rejoint l'event (" + event.getParticipantCount() + "/" + event.getMaxPlayers() + ")");
        
        // Auto-start if full
        if (event.isFull() && event.getState() == FFAEvent.FFAState.WAITING) {
            startFFA(eventId);
        }
        
        return true;
    }
    
    public void leaveFFA(Player player) {
        UUID eventId = playerToEvent.get(player.getUniqueId());
        if (eventId == null) return;
        
        FFAEvent event = activeEvents.get(eventId);
        if (event == null) return;
        
        event.removeParticipant(player.getUniqueId());
        playerToEvent.remove(player.getUniqueId());
        
        // Restore player
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        practicePlayer.setState(PlayerState.SPAWN);
        player.teleport(plugin.getSpawnLocation());
        plugin.getInventoryManager().giveSpawnItems(player);
        
        player.sendMessage(ChatColor.RED + "Vous avez quitté le FFA!");
        
        // Check if event should end
        if (event.getParticipantCount() < 2 && event.getState() == FFAEvent.FFAState.ACTIVE) {
            endFFA(eventId);
        }
    }
    
    private void startFFA(UUID eventId) {
        FFAEvent event = activeEvents.get(eventId);
        if (event == null) return;
        
        broadcastEvent(event, ChatColor.GOLD + "═══════════════════════════════");
        broadcastEvent(event, ChatColor.GREEN + "⚔ FFA " + event.getKit() + " commence!");
        broadcastEvent(event, ChatColor.YELLOW + "Éliminez le maximum de joueurs!");
        broadcastEvent(event, ChatColor.GOLD + "═══════════════════════════════");
        
        // Start scoreboard updates
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
        }
        
        scoreboardTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID playerId : event.getParticipants()) {
                Player p = Bukkit.getPlayer(playerId);
                if (p != null && p.isOnline()) {
                    updateFFAScoreboard(p, event);
                }
            }
        }, 0L, 20L);
    }
    
    public void handleKill(Player killer, Player victim) {
        UUID eventId = playerToEvent.get(killer.getUniqueId());
        if (eventId == null) return;
        
        FFAEvent event = activeEvents.get(eventId);
        if (event == null) return;
        
        event.addKill(killer.getUniqueId());
        
        // Reward coins
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(killer.getUniqueId());
        practicePlayer.addCoins(10);
        
        killer.sendMessage(ChatColor.GREEN + "+10 coins!");
        
        // Respawn victim
        victim.spigot().respawn();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            victim.teleport(event.getSpawnLocation());
            Kit kit = plugin.getKitManager().getKit(event.getKit());
            if (kit != null) {
                victim.getInventory().setContents(kit.getContents());
                victim.getInventory().setArmorContents(kit.getArmor());
            }
        }, 5L);
        
        broadcastEvent(event, ChatColor.YELLOW + killer.getName() + ChatColor.GRAY + 
            " a tué " + ChatColor.YELLOW + victim.getName() + ChatColor.GRAY + 
            " (" + event.getKills(killer.getUniqueId()) + " kills)");
    }
    
    private void endFFA(UUID eventId) {
        FFAEvent event = activeEvents.get(eventId);
        if (event == null) return;
        
        broadcastMessage(ChatColor.GOLD + "═══════════════════════════════");
        broadcastMessage(ChatColor.YELLOW + "FFA " + event.getKit() + " terminé!");
        
        List<Map.Entry<UUID, Integer>> top = event.getTopKillers(3);
        for (int i = 0; i < top.size(); i++) {
            Player p = Bukkit.getPlayer(top.get(i).getKey());
            if (p != null) {
                String position = i == 0 ? ChatColor.GOLD + "#1" : 
                                 i == 1 ? ChatColor.YELLOW + "#2" : 
                                 ChatColor.RED + "#3";
                broadcastMessage(position + " " + ChatColor.WHITE + p.getName() + 
                    ChatColor.GRAY + " - " + ChatColor.YELLOW + top.get(i).getValue() + " kills");
                
                // Rewards
                int reward = i == 0 ? 500 : i == 1 ? 300 : 100;
                PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(p.getUniqueId());
                practicePlayer.addCoins(reward);
                p.sendMessage(ChatColor.GOLD + "Récompense: +" + reward + " coins!");
            }
        }
        
        broadcastMessage(ChatColor.GOLD + "═══════════════════════════════");
        
        // Cleanup
        for (UUID playerId : new HashSet<>(event.getParticipants())) {
            Player p = Bukkit.getPlayer(playerId);
            if (p != null) {
                leaveFFA(p);
            }
        }
        
        activeEvents.remove(eventId);
        
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
            scoreboardTask = null;
        }
    }
    
    private void updateFFAScoreboard(Player player, FFAEvent event) {
        // Update scoreboard with FFA stats
        // This would integrate with CustomScoreboardManager
    }
    
    public FFAEvent getPlayerEvent(UUID player) {
        UUID eventId = playerToEvent.get(player);
        return eventId != null ? activeEvents.get(eventId) : null;
    }
    
    public List<FFAEvent> getActiveEvents() {
        return new ArrayList<>(activeEvents.values());
    }
    
    private void broadcastEvent(FFAEvent event, String message) {
        for (UUID playerId : event.getParticipants()) {
            Player p = Bukkit.getPlayer(playerId);
            if (p != null && p.isOnline()) {
                p.sendMessage(message);
            }
        }
    }
    
    private void broadcastMessage(String message) {
        Bukkit.broadcastMessage(message);
    }
}
