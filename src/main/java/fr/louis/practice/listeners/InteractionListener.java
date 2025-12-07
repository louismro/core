package fr.louis.practice.listeners;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.gui.QueueSelectorGUI;
import fr.louis.practice.models.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InteractionListener implements Listener {
    private final PracticeCore plugin;
    private final QueueSelectorGUI queueGUI;
    
    public InteractionListener(PracticeCore plugin) {
        this.plugin = plugin;
        this.queueGUI = new QueueSelectorGUI(plugin);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || !item.hasItemMeta()) {
            return;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer == null) {
            return;
        }
        
        // Gérer les items de spawn
        if (practicePlayer.getState() == PlayerState.SPAWN) {
            handleSpawnItems(player, item, event);
            return;
        }
        
        // Gérer les items de queue
        if (practicePlayer.getState() == PlayerState.QUEUE) {
            handleQueueItems(player, item, event);
            return;
        }
        
        // Gérer les items de spectateur
        if (practicePlayer.getState() == PlayerState.SPECTATING) {
            handleSpectatorItems(player, item, event);
            return;
        }
        
        // Gérer les perles d'ender
        if (item.getType() == Material.ENDER_PEARL) {
            handleEnderPearl(player, event);
            return;
        }
        
        // Gérer les potions splash
        if (item.getType() == Material.SPLASH_POTION) {
            handlePotion(player, event);
        }
    }
    
    private void handleSpawnItems(Player player, ItemStack item, PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && 
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        String name = meta.getDisplayName();
        
        if (name.contains("Queues Non-Classées")) {
            queueGUI.openUnrankedQueues(player);
        } else if (name.contains("Queues Classées")) {
            queueGUI.openRankedQueues(player);
        } else if (name.contains("Créer une Partie")) {
            createParty(player);
        } else if (name.contains("Éditer les Kits")) {
            player.sendMessage("§eÉdition de kits - En cours de développement");
        } else if (name.contains("Statistiques")) {
            showStatistics(player);
        }
    }
    
    private void handleQueueItems(Player player, ItemStack item, PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && 
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        String name = meta.getDisplayName();
        
        if (name.contains("Quitter la Queue")) {
            plugin.getQueueManager().removeFromQueue(player);
        }
    }
    
    private void handleSpectatorItems(Player player, ItemStack item, PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && 
            event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        event.setCancelled(true);
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;
        String name = meta.getDisplayName();
        
        if (name.contains("Téléportation")) {
            player.sendMessage("§eTéléportation - En cours de développement");
        } else if (name.contains("Quitter")) {
            player.teleport(plugin.getSpawnLocation());
            PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
            if (practicePlayer != null) {
                practicePlayer.setState(PlayerState.SPAWN);
            }
            plugin.getInventoryManager().giveSpawnItems(player);
        }
    }
    
    @SuppressWarnings({"all", "unused"})
    private void handleEnderPearl(Player player, PlayerInteractEvent event) {
        // Pas de cooldown - utilisation libre
    }
    
    @SuppressWarnings({"all", "unused"})
    private void handlePotion(Player player, PlayerInteractEvent event) {
        // Statistiques pour les potions
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer != null && practicePlayer.isInMatch()) {
            Match match = practicePlayer.getCurrentMatch();
            MatchPlayerData data = match.getPlayerData(player.getUniqueId());
            data.addPotionUsed();
        }
    }
    
    private void createParty(Player player) {
        Party party = plugin.getPartyManager().createParty(player);
        if (party != null) {
            player.sendMessage("§aPartie créée! Utilisez §e/party invite <joueur> §apour inviter");
        } else {
            player.sendMessage("§cVous êtes déjà dans une partie!");
        }
    }
    
    private void showStatistics(Player player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        if (practicePlayer == null) {
            return;
        }
        
        player.sendMessage("§8§m----------------------------");
        player.sendMessage("§b§lStatistiques de " + player.getName());
        player.sendMessage("");
        player.sendMessage("§7Killstreak actuel: §e" + practicePlayer.getCurrentStreak());
        player.sendMessage("§7Killstreak maximum: §e" + practicePlayer.getHighestStreak());
        player.sendMessage("");
        
        for (String kit : practicePlayer.getElo().keySet()) {
            int elo = practicePlayer.getElo(kit);
            PlayerStats stats = practicePlayer.getStats(kit);
            
            player.sendMessage("§e" + kit + "§7:");
            player.sendMessage("  §7ELO: §e" + elo + " §7(" + 
                plugin.getEloManager().getRank(elo) + "§7)");
            player.sendMessage("  §7K/D: §e" + String.format("%.2f", stats.getKDRatio()));
            player.sendMessage("  §7Victoires: §a" + stats.getWins() + " §7Défaites: §c" + stats.getLosses());
        }
        
        player.sendMessage("§8§m----------------------------");
    }
}
