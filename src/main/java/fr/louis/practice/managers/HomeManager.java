package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Home;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HomeManager {
    @SuppressWarnings("unused")
    private final PracticeCore plugin;
    
    @Getter
    private final Map<UUID, Map<String, Home>> homes = new ConcurrentHashMap<>();
    
    private static final int MAX_HOMES_DEFAULT = 3;
    private static final int MAX_HOMES_VIP = 10;
    
    public HomeManager(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    public void setHome(Player player, String name, Location location) {
        UUID playerId = player.getUniqueId();
        Map<String, Home> playerHomes = homes.computeIfAbsent(playerId, k -> new HashMap<>());
        
        int maxHomes = player.hasPermission("practice.home.vip") ? MAX_HOMES_VIP : MAX_HOMES_DEFAULT;
        
        if (!playerHomes.containsKey(name.toLowerCase()) && playerHomes.size() >= maxHomes) {
            player.sendMessage(ChatColor.RED + "Limite de homes atteinte! (" + maxHomes + " max)");
            return;
        }
        
        Home home = new Home(playerId, name.toLowerCase(), location);
        playerHomes.put(name.toLowerCase(), home);
        
        player.sendMessage(ChatColor.GREEN + "✓ Home '" + ChatColor.YELLOW + name + 
            ChatColor.GREEN + "' défini!");
    }
    
    public void deleteHome(Player player, String name) {
        Map<String, Home> playerHomes = homes.get(player.getUniqueId());
        
        if (playerHomes == null || !playerHomes.containsKey(name.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Home introuvable!");
            return;
        }
        
        playerHomes.remove(name.toLowerCase());
        player.sendMessage(ChatColor.YELLOW + "Home '" + name + "' supprimé.");
    }
    
    public void teleportHome(Player player, String name) {
        Map<String, Home> playerHomes = homes.get(player.getUniqueId());
        
        if (playerHomes == null || !playerHomes.containsKey(name.toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Home introuvable!");
            return;
        }
        
        Home home = playerHomes.get(name.toLowerCase());
        player.teleport(home.getLocation());
        player.sendMessage(ChatColor.GREEN + "✓ Téléporté à: " + ChatColor.YELLOW + name);
    }
    
    public List<Home> getHomes(UUID playerId) {
        Map<String, Home> playerHomes = homes.get(playerId);
        return playerHomes != null ? new ArrayList<>(playerHomes.values()) : new ArrayList<>();
    }
}
