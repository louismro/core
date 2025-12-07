package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DuelManager {
    private final PracticeCore plugin;
    private final Map<UUID, DuelRequest> activeRequests;
    
    public DuelManager(PracticeCore plugin) {
        this.plugin = plugin;
        this.activeRequests = new ConcurrentHashMap<>();
        
        // Nettoyer les anciennes demandes
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            long now = System.currentTimeMillis();
            activeRequests.entrySet().removeIf(entry -> 
                now - entry.getValue().getTimestamp() > 60000); // 60 secondes
        }, 0L, 20L * 10L);
    }
    
    public void sendDuelRequest(Player sender, Player target, String kitName) {
        PracticePlayer senderData = plugin.getPlayerManager().getPlayer(sender);
        PracticePlayer targetData = plugin.getPlayerManager().getPlayer(target);
        
        if (senderData == null || targetData == null) {
            return;
        }
        
        if (!targetData.isAllowDuels()) {
            sender.sendMessage("§cCe joueur n'accepte pas les duels!");
            return;
        }
        
        if (senderData.isInMatch() || targetData.isInMatch()) {
            sender.sendMessage("§cCe joueur est déjà en match!");
            return;
        }
        
        if (senderData.isInQueue() || targetData.isInQueue()) {
            sender.sendMessage("§cCe joueur est déjà en queue!");
            return;
        }
        
        Kit kit = plugin.getKitManager().getKit(kitName);
        if (kit == null) {
            sender.sendMessage("§cKit introuvable!");
            return;
        }
        
        DuelRequest request = new DuelRequest(
            sender.getUniqueId(),
            target.getUniqueId(),
            kitName,
            System.currentTimeMillis()
        );
        
        activeRequests.put(target.getUniqueId(), request);
        
        sender.sendMessage("§aDemande de duel envoyée à §e" + target.getName());
        target.sendMessage("§e" + sender.getName() + " §avous défie en duel (§e" + kit.getDisplayName() + "§a)");
        target.sendMessage("§a/duel accept " + sender.getName() + " §7pour accepter");
    }
    
    public void acceptDuel(Player accepter) {
        DuelRequest request = activeRequests.get(accepter.getUniqueId());
        
        if (request == null) {
            accepter.sendMessage("§cVous n'avez aucune demande de duel en attente!");
            return;
        }
        
        Player sender = Bukkit.getPlayer(request.getSender());
        
        if (sender == null) {
            accepter.sendMessage("§cLe joueur n'est plus en ligne!");
            activeRequests.remove(accepter.getUniqueId());
            return;
        }
        
        activeRequests.remove(accepter.getUniqueId());
        
        // Créer le match
        Match match = plugin.getMatchManager().createMatch(
            MatchType.SOLO,
            request.getKitName(),
            false,
            false
        );
        
        if (match == null) {
            sender.sendMessage("§cErreur: Aucune arène disponible!");
            accepter.sendMessage("§cErreur: Aucune arène disponible!");
            return;
        }
        
        // Créer les équipes
        MatchTeam team1 = new MatchTeam(sender.getName());
        team1.addPlayer(sender.getUniqueId());
        
        MatchTeam team2 = new MatchTeam(accepter.getName());
        team2.addPlayer(accepter.getUniqueId());
        
        match.addTeam(team1);
        match.addTeam(team2);
        
        sender.sendMessage("§a" + accepter.getName() + " §aa accepté votre duel!");
        accepter.sendMessage("§aVous avez accepté le duel de §e" + sender.getName());
        
        // Démarrer le countdown
        startCountdown(match);
    }
    
    public void denyDuel(Player denier) {
        DuelRequest request = activeRequests.remove(denier.getUniqueId());
        
        if (request == null) {
            denier.sendMessage("§cVous n'avez aucune demande de duel en attente!");
            return;
        }
        
        Player sender = Bukkit.getPlayer(request.getSender());
        
        if (sender != null) {
            sender.sendMessage("§c" + denier.getName() + " §ca refusé votre duel!");
        }
        
        denier.sendMessage("§cVous avez refusé le duel");
    }
    
    private void startCountdown(Match match) {
        Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
            int countdown = 5;
            
            @Override
            public void run() {
                if (countdown <= 0) {
                    plugin.getMatchManager().startMatch(match);
                    return;
                }
                
                for (UUID uuid : match.getAllPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendMessage("§eLe match commence dans §c" + countdown + "§e...");
                    }
                }
                
                countdown--;
            }
        }, 0L, 20L);
    }
    
    @Getter
    public static class DuelRequest {
        private final UUID sender;
        private final UUID target;
        private final String kitName;
        private final long timestamp;
        
        public DuelRequest(UUID sender, UUID target, String kitName, long timestamp) {
            this.sender = sender;
            this.target = target;
            this.kitName = kitName;
            this.timestamp = timestamp;
        }
    }
}
