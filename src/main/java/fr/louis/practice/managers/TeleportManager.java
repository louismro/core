package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.TeleportRequest;
import fr.louis.practice.models.TeleportRequest.TeleportType;
import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<UUID, TeleportRequest> pendingRequests = new HashMap<>();
    
    @Getter
    private final Map<UUID, Location> backLocations = new HashMap<>();
    
    private final Map<UUID, Long> teleportCooldowns = new HashMap<>();
    private static final long COOLDOWN_MS = 3000;
    
    public TeleportManager(PracticeCore plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }
    
    public void sendTeleportRequest(Player sender, Player target, TeleportType type) {
        if (pendingRequests.containsKey(target.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Ce joueur a déjà une demande en attente!");
            return;
        }
        
        TeleportRequest request = new TeleportRequest(sender.getUniqueId(), target.getUniqueId(), type);
        pendingRequests.put(target.getUniqueId(), request);
        
        String message = type == TeleportType.TPA ?
            sender.getName() + " veut se téléporter à vous" :
            sender.getName() + " veut vous téléporter à lui";
        
        sender.sendMessage(ChatColor.GREEN + "✓ Demande envoyée à " + target.getName());
        
        target.sendMessage("");
        target.sendMessage(ChatColor.YELLOW + message + "!");
        
        TextComponent accept = new TextComponent(ChatColor.GREEN + " [ACCEPTER] ");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new net.md_5.bungee.api.chat.hover.content.Text("Cliquez pour accepter")));
        
        TextComponent deny = new TextComponent(ChatColor.RED + " [REFUSER]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
            new net.md_5.bungee.api.chat.hover.content.Text("Cliquez pour refuser")));
        
        target.spigot().sendMessage(accept, deny);
        target.sendMessage("");
    }
    
    public void acceptRequest(Player acceptor) {
        TeleportRequest request = pendingRequests.remove(acceptor.getUniqueId());
        
        if (request == null || request.isExpired()) {
            acceptor.sendMessage(ChatColor.RED + "Aucune demande valide!");
            return;
        }
        
        Player sender = plugin.getServer().getPlayer(request.getSenderId());
        
        if (sender == null) {
            acceptor.sendMessage(ChatColor.RED + "Le joueur n'est plus connecté!");
            return;
        }
        
        org.bukkit.Location senderLoc = sender.getLocation();
        org.bukkit.Location acceptorLoc = acceptor.getLocation();
        
        if (request.getType() == TeleportType.TPA) {
            if (senderLoc != null && acceptorLoc != null) {
                backLocations.put(sender.getUniqueId(), senderLoc);
                sender.teleport(acceptorLoc);
                sender.sendMessage(ChatColor.GREEN + "✓ Téléporté à " + acceptor.getName());
            }
        } else {
            if (senderLoc != null && acceptorLoc != null) {
                backLocations.put(acceptor.getUniqueId(), acceptorLoc);
                acceptor.teleport(senderLoc);
                acceptor.sendMessage(ChatColor.GREEN + "✓ Téléporté à " + sender.getName());
            }
        }
        
        acceptor.sendMessage(ChatColor.GREEN + "✓ Demande acceptée!");
    }
    
    public void denyRequest(Player denier) {
        TeleportRequest request = pendingRequests.remove(denier.getUniqueId());
        
        if (request == null) {
            denier.sendMessage(ChatColor.RED + "Aucune demande valide!");
            return;
        }
        
        Player sender = plugin.getServer().getPlayer(request.getSenderId());
        if (sender != null) {
            sender.sendMessage(ChatColor.RED + denier.getName() + " a refusé votre demande.");
        }
        
        denier.sendMessage(ChatColor.YELLOW + "Demande refusée.");
    }
    
    public void teleportWithDelay(Player player, Location location, int delaySeconds) {
        if (hasCooldown(player.getUniqueId())) {
            long remaining = getRemainingCooldown(player.getUniqueId());
            player.sendMessage(ChatColor.RED + "Attendez " + (remaining / 1000) + "s!");
            return;
        }
        
        player.sendMessage(ChatColor.YELLOW + "Téléportation dans " + delaySeconds + " secondes...");
        player.sendMessage(ChatColor.RED + "Ne bougez pas!");
        
        Location startLoc = player.getLocation();
        if (startLoc == null) return;
        final Location finalStartLoc = startLoc.clone();
        
        new BukkitRunnable() {
            int countdown = delaySeconds;
            
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                
                Location currentLoc = player.getLocation();
                // Vérifier si le joueur a bougé
                if (currentLoc != null && currentLoc.distance(finalStartLoc) > 0.5) {
                    player.sendMessage(ChatColor.RED + "✗ Téléportation annulée (vous avez bougé)!");
                    cancel();
                    return;
                }
                
                if (countdown <= 0) {
                    backLocations.put(player.getUniqueId(), finalStartLoc);
                    player.teleport(location);
                    player.sendMessage(ChatColor.GREEN + "✓ Téléporté!");
                    teleportCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                    cancel();
                    return;
                }
                
                if (countdown <= 3) {
                    player.sendMessage(ChatColor.YELLOW + "" + countdown + "...");
                }
                
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
    
    public void teleportBack(Player player) {
        Location backLoc = backLocations.get(player.getUniqueId());
        
        if (backLoc == null) {
            player.sendMessage(ChatColor.RED + "Aucune position précédente!");
            return;
        }
        
        player.teleport(backLoc);
        player.sendMessage(ChatColor.GREEN + "✓ Retour à votre position précédente!");
        backLocations.remove(player.getUniqueId());
    }
    
    private boolean hasCooldown(UUID playerId) {
        Long lastTp = teleportCooldowns.get(playerId);
        if (lastTp == null) return false;
        return System.currentTimeMillis() - lastTp < COOLDOWN_MS;
    }
    
    private long getRemainingCooldown(UUID playerId) {
        Long lastTp = teleportCooldowns.get(playerId);
        if (lastTp == null) return 0;
        long elapsed = System.currentTimeMillis() - lastTp;
        return Math.max(0, COOLDOWN_MS - elapsed);
    }
    
    private void startCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                pendingRequests.entrySet().removeIf(entry -> entry.getValue().isExpired());
            }
        }.runTaskTimerAsynchronously(plugin, 1200L, 1200L);
    }
}
