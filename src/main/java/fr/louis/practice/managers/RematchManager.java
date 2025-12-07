package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class RematchManager {
    private final PracticeCore plugin;
    private final Map<UUID, RematchRequest> pendingRematches;
    
    public RematchManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.pendingRematches = new HashMap<>();
    }
    
    public void sendRematchRequest(Player sender, Player target, String kitName, boolean ranked) {
        RematchRequest request = new RematchRequest(
            sender.getUniqueId(),
            target.getUniqueId(),
            kitName,
            ranked,
            System.currentTimeMillis()
        );
        
        pendingRematches.put(target.getUniqueId(), request);
        
        sender.sendMessage("§aVous avez envoyé une demande de rematch à §e" + target.getName() + "§a!");
        target.sendMessage("§e" + sender.getName() + " §avous propose un rematch en §e" + kitName + "§a!");
        target.sendMessage("§7Utilisez §e/rematch accept §7pour accepter!");
        
        // Auto-expire après 30 secondes
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (pendingRematches.containsKey(target.getUniqueId())) {
                RematchRequest req = pendingRematches.get(target.getUniqueId());
                if (req.getTimestamp() == request.getTimestamp()) {
                    pendingRematches.remove(target.getUniqueId());
                    Player s = plugin.getServer().getPlayer(sender.getUniqueId());
                    if (s != null && s.isOnline()) {
                        s.sendMessage("§cVotre demande de rematch a expiré.");
                    }
                }
            }
        }, 20L * 30);
    }
    
    public boolean hasRematchRequest(UUID player) {
        return pendingRematches.containsKey(player);
    }
    
    public RematchRequest getRematchRequest(UUID player) {
        return pendingRematches.get(player);
    }
    
    public void removeRematchRequest(UUID player) {
        pendingRematches.remove(player);
    }
    
    @Getter
    public static class RematchRequest {
        private final UUID sender;
        private final UUID target;
        private final String kitName;
        private final boolean ranked;
        private final long timestamp;
        
        public RematchRequest(UUID sender, UUID target, String kitName, boolean ranked, long timestamp) {
            this.sender = sender;
            this.target = target;
            this.kitName = kitName;
            this.ranked = ranked;
            this.timestamp = timestamp;
        }
    }
}
