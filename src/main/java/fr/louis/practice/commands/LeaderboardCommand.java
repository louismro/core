package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.managers.LeaderboardManager.LeaderboardEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaderboardCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public LeaderboardCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande est réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // Leaderboard global
            displayGlobalLeaderboard(player);
        } else {
            // Leaderboard par kit
            String kitName = args[0];
            if (!plugin.getKitManager().getKits().containsKey(kitName)) {
                player.sendMessage("§cKit '" + kitName + "' introuvable!");
                return true;
            }
            displayKitLeaderboard(player, kitName);
        }
        
        return true;
    }
    
    private void displayGlobalLeaderboard(Player player) {
        List<LeaderboardEntry> top = plugin.getLeaderboardManager().getGlobalTop(10);
        
        player.sendMessage("§8§m                                    ");
        player.sendMessage("§6§lLEADERBOARD GLOBAL");
        player.sendMessage("");
        
        int rank = 1;
        for (LeaderboardEntry entry : top) {
            String rankColor = getRankColor(rank);
            player.sendMessage(rankColor + "#" + rank + " §f" + entry.getName() + 
                " §7- §e" + entry.getElo() + " ELO §7(" + entry.getWins() + "W/" + entry.getLosses() + "L)");
            rank++;
        }
        
        player.sendMessage("");
        int playerPos = plugin.getLeaderboardManager().getPlayerPosition(player.getUniqueId(), null);
        if (playerPos > 0) {
            player.sendMessage("§7Votre position: §e#" + playerPos);
        }
        player.sendMessage("§8§m                                    ");
    }
    
    private void displayKitLeaderboard(Player player, String kitName) {
        List<LeaderboardEntry> top = plugin.getLeaderboardManager().getKitTop(kitName, 10);
        
        player.sendMessage("§8§m                                    ");
        player.sendMessage("§6§lLEADERBOARD " + kitName.toUpperCase());
        player.sendMessage("");
        
        int rank = 1;
        for (LeaderboardEntry entry : top) {
            String rankColor = getRankColor(rank);
            player.sendMessage(rankColor + "#" + rank + " §f" + entry.getName() + 
                " §7- §e" + entry.getElo() + " ELO §7(" + 
                String.format("%.2f", entry.getKd()) + " K/D)");
            rank++;
        }
        
        player.sendMessage("");
        int playerPos = plugin.getLeaderboardManager().getPlayerPosition(player.getUniqueId(), kitName);
        if (playerPos > 0) {
            player.sendMessage("§7Votre position: §e#" + playerPos);
        }
        player.sendMessage("§8§m                                    ");
    }
    
    private String getRankColor(int rank) {
        return switch (rank) {
            case 1 -> "§6§l";
            case 2 -> "§e§l";
            case 3 -> "§c§l";
            default -> "§7";
        };
    }
}
