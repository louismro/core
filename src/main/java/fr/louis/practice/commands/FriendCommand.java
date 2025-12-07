package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FriendCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public FriendCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showHelp(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "add":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /friend add <joueur>");
                    return true;
                }
                handleAdd(player, args[1]);
                break;
                
            case "remove":
            case "delete":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /friend remove <joueur>");
                    return true;
                }
                handleRemove(player, args[1]);
                break;
                
            case "accept":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /friend accept <joueur>");
                    return true;
                }
                handleAccept(player, args[1]);
                break;
                
            case "deny":
            case "decline":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /friend deny <joueur>");
                    return true;
                }
                handleDeny(player, args[1]);
                break;
                
            case "list":
                handleList(player);
                break;
                
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "COMMANDES AMIS" + ChatColor.GOLD + "          ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/friend add <joueur>");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + 
            "Envoyer une demande");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/friend remove <joueur>");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Retirer un ami");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/friend accept <joueur>");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Accepter une demande");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/friend deny <joueur>");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Refuser une demande");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "/friend list");
        player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + "Voir vos amis");
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
    
    private void handleAdd(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return;
        }
        
        plugin.getFriendManager().sendFriendRequest(player.getUniqueId(), target.getUniqueId());
    }
    
    private void handleRemove(Player player, String targetName) {
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return;
        }
        
        if (!plugin.getFriendManager().areFriends(player.getUniqueId(), target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Ce joueur n'est pas dans votre liste d'amis!");
            return;
        }
        
        plugin.getFriendManager().removeFriend(player.getUniqueId(), target.getUniqueId());
    }
    
    private void handleAccept(Player player, String senderName) {
        Player sender = Bukkit.getPlayer(senderName);
        
        if (sender == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return;
        }
        
        plugin.getFriendManager().acceptRequest(player.getUniqueId(), sender.getUniqueId());
    }
    
    private void handleDeny(Player player, String senderName) {
        Player sender = Bukkit.getPlayer(senderName);
        
        if (sender == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return;
        }
        
        plugin.getFriendManager().denyRequest(player.getUniqueId(), sender.getUniqueId());
    }
    
    private void handleList(Player player) {
        Set<UUID> friends = plugin.getFriendManager().getFriends(player.getUniqueId());
        List<UUID> onlineFriends = plugin.getFriendManager().getOnlineFriends(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "VOS AMIS" + ChatColor.GOLD + 
            " (" + onlineFriends.size() + "/" + friends.size() + ")       ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        if (friends.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Aucun ami");
        } else {
            for (UUID friendId : friends) {
                Player friend = plugin.getServer().getPlayer(friendId);
                String name = friend != null ? friend.getName() : "Inconnu";
                boolean online = friend != null;
                
                String status = online ? ChatColor.GREEN + "●" : ChatColor.RED + "●";
                player.sendMessage(ChatColor.GOLD + "║  " + status + " " + 
                    ChatColor.WHITE + name);
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
}
