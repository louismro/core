package fr.louis.practice.managers;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.FriendRequest;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FriendManager {
    private final PracticeCore plugin;
    
    @Getter
    private final Map<UUID, Set<UUID>> friends = new ConcurrentHashMap<>();
    
    @Getter
    private final Map<UUID, List<FriendRequest>> pendingRequests = new ConcurrentHashMap<>();
    
    private static final int MAX_FRIENDS = 100;
    
    public FriendManager(PracticeCore plugin) {
        this.plugin = plugin;
        startRequestCleanupTask();
    }
    
    public void sendFriendRequest(UUID senderId, UUID targetId) {
        Player sender = plugin.getServer().getPlayer(senderId);
        Player target = plugin.getServer().getPlayer(targetId);
        
        if (sender == null || target == null) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "Ce joueur n'est pas en ligne!");
            }
            return;
        }
        
        if (senderId.equals(targetId)) {
            sender.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous ajouter vous-même!");
            return;
        }
        
        if (areFriends(senderId, targetId)) {
            sender.sendMessage(ChatColor.RED + "Vous êtes déjà amis avec " + target.getName() + "!");
            return;
        }
        
        if (hasPendingRequest(senderId, targetId)) {
            sender.sendMessage(ChatColor.RED + "Vous avez déjà une demande en attente avec " + 
                target.getName() + "!");
            return;
        }
        
        Set<UUID> senderFriends = friends.getOrDefault(senderId, new HashSet<>());
        if (senderFriends.size() >= MAX_FRIENDS) {
            sender.sendMessage(ChatColor.RED + "Vous avez atteint la limite de " + 
                MAX_FRIENDS + " amis!");
            return;
        }
        
        FriendRequest request = new FriendRequest(senderId, targetId);
        pendingRequests.computeIfAbsent(targetId, k -> new ArrayList<>()).add(request);
        
        sender.sendMessage(ChatColor.GREEN + "Demande d'ami envoyée à " + 
            ChatColor.YELLOW + target.getName() + ChatColor.GREEN + "!");
        
        target.sendMessage("");
        target.sendMessage(ChatColor.GREEN + "━━━━━━━━━━━━━━━━━━━━━━━━━━");
        target.sendMessage(ChatColor.YELLOW + sender.getName() + ChatColor.WHITE + 
            " vous a envoyé une demande d'ami!");
        target.sendMessage("");
        target.sendMessage(ChatColor.GREEN + "  [ACCEPTER] " + ChatColor.GRAY + 
            "/friend accept " + sender.getName());
        target.sendMessage(ChatColor.RED + "  [REFUSER] " + ChatColor.GRAY + 
            "/friend deny " + sender.getName());
        target.sendMessage(ChatColor.GREEN + "━━━━━━━━━━━━━━━━━━━━━━━━━━");
        target.sendMessage("");
    }
    
    public void acceptRequest(UUID acceptorId, UUID requesterId) {
        Player acceptor = plugin.getServer().getPlayer(acceptorId);
        Player requester = plugin.getServer().getPlayer(requesterId);
        
        if (acceptor == null) return;
        
        List<FriendRequest> requests = pendingRequests.get(acceptorId);
        if (requests == null) {
            acceptor.sendMessage(ChatColor.RED + "Aucune demande de ce joueur!");
            return;
        }
        
        FriendRequest request = requests.stream()
            .filter(r -> r.getSenderId().equals(requesterId))
            .findFirst()
            .orElse(null);
        
        if (request == null) {
            acceptor.sendMessage(ChatColor.RED + "Aucune demande de ce joueur!");
            return;
        }
        
        if (request.isExpired()) {
            requests.remove(request);
            acceptor.sendMessage(ChatColor.RED + "Cette demande a expiré!");
            return;
        }
        
        // Ajouter les deux comme amis
        friends.computeIfAbsent(acceptorId, k -> ConcurrentHashMap.newKeySet()).add(requesterId);
        friends.computeIfAbsent(requesterId, k -> ConcurrentHashMap.newKeySet()).add(acceptorId);
        
        requests.remove(request);
        
        acceptor.sendMessage(ChatColor.GREEN + "✓ Vous êtes maintenant amis avec " + 
            ChatColor.YELLOW + (requester != null ? requester.getName() : "ce joueur") + 
            ChatColor.GREEN + "!");
        
        if (requester != null) {
            requester.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.YELLOW + 
                acceptor.getName() + ChatColor.GREEN + " a accepté votre demande d'ami!");
        }
    }
    
    public void denyRequest(UUID denierId, UUID requesterId) {
        Player denier = plugin.getServer().getPlayer(denierId);
        
        if (denier == null) return;
        
        List<FriendRequest> requests = pendingRequests.get(denierId);
        if (requests == null) {
            denier.sendMessage(ChatColor.RED + "Aucune demande de ce joueur!");
            return;
        }
        
        boolean removed = requests.removeIf(r -> r.getSenderId().equals(requesterId));
        
        if (removed) {
            denier.sendMessage(ChatColor.YELLOW + "Demande d'ami refusée.");
        } else {
            denier.sendMessage(ChatColor.RED + "Aucune demande de ce joueur!");
        }
    }
    
    public void removeFriend(UUID playerId, UUID friendId) {
        Set<UUID> playerFriends = friends.get(playerId);
        Set<UUID> friendFriends = friends.get(friendId);
        
        if (playerFriends != null) {
            playerFriends.remove(friendId);
        }
        
        if (friendFriends != null) {
            friendFriends.remove(playerId);
        }
        
        Player player = plugin.getServer().getPlayer(playerId);
        
        if (player != null) {
            player.sendMessage(ChatColor.YELLOW + "Ami retiré de votre liste.");
        }
    }
    
    public boolean areFriends(UUID player1, UUID player2) {
        Set<UUID> player1Friends = friends.get(player1);
        return player1Friends != null && player1Friends.contains(player2);
    }
    
    public Set<UUID> getFriends(UUID playerId) {
        return friends.getOrDefault(playerId, new HashSet<>());
    }
    
    public List<UUID> getOnlineFriends(UUID playerId) {
        Set<UUID> playerFriends = getFriends(playerId);
        List<UUID> online = new ArrayList<>();
        
        for (UUID friendId : playerFriends) {
            if (plugin.getServer().getPlayer(friendId) != null) {
                online.add(friendId);
            }
        }
        
        return online;
    }
    
    private boolean hasPendingRequest(UUID senderId, UUID targetId) {
        List<FriendRequest> requests = pendingRequests.get(targetId);
        if (requests == null) return false;
        
        return requests.stream().anyMatch(r -> r.getSenderId().equals(senderId));
    }
    
    private void startRequestCleanupTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (List<FriendRequest> requests : pendingRequests.values()) {
                    requests.removeIf(FriendRequest::isExpired);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 6000L, 6000L); // Chaque 5 minutes
    }
}
