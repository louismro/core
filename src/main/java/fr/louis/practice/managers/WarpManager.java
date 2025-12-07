package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Warp;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpManager {
    @Getter
    private final Map<String, Warp> warps = new HashMap<>();
    
    public WarpManager(PracticeCore plugin) {
        // Plugin passé pour compatibilité API mais non stocké
    }
    
    public void createWarp(String name, Location location, UUID creatorId) {
        Warp warp = new Warp(name.toLowerCase(), location, creatorId);
        warps.put(name.toLowerCase(), warp);
    }
    
    public void deleteWarp(String name) {
        warps.remove(name.toLowerCase());
    }
    
    public Warp getWarp(String name) {
        return warps.get(name.toLowerCase());
    }
    
    public void teleportToWarp(Player player, String warpName) {
        Warp warp = getWarp(warpName);
        
        if (warp == null) {
            player.sendMessage(ChatColor.RED + "Warp introuvable!");
            return;
        }
        
        if (warp.getPermission() != null && !player.hasPermission(warp.getPermission())) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas la permission d'utiliser ce warp!");
            return;
        }
        
        player.teleport(warp.getLocation());
        player.sendMessage(ChatColor.GREEN + "✓ Téléporté à: " + ChatColor.YELLOW + warp.getDisplayName());
    }
}
