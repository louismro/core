package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DailyCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public DailyCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            }
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length > 0 && args[0].equalsIgnoreCase("preview")) {
            showPreview(player);
            return true;
        }
        
        if (args.length > 0 && args[0].equalsIgnoreCase("stats")) {
            showStats(player);
            return true;
        }
        
        // Claim reward
        plugin.getDailyRewardManager().claimReward(player);
        
        return true;
    }
    
    private void showPreview(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ Aperçu des Récompenses ═══");
        player.sendMessage("");
        
        for (String line : plugin.getDailyRewardManager().getRewardPreview(player.getUniqueId())) {
            player.sendMessage(line);
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Les récompenses augmentent avec votre série!");
        player.sendMessage(ChatColor.GRAY + "Bonus: +50 coins tous les 7 jours");
    }
    
    private void showStats(Player player) {
        int streak = plugin.getDailyRewardManager().getStreak(player.getUniqueId());
        LocalDate lastClaim = plugin.getDailyRewardManager().getLastClaim(player.getUniqueId());
        String status = plugin.getDailyRewardManager().getStreakStatus(player.getUniqueId());
        int nextReward = plugin.getDailyRewardManager().getNextRewardCoins(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ Statistiques Quotidiennes ═══");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Série actuelle: " + ChatColor.AQUA + streak + " jours");
        
        if (lastClaim != null) {
            long daysAgo = ChronoUnit.DAYS.between(lastClaim, LocalDate.now());
            player.sendMessage(ChatColor.YELLOW + "Dernier claim: " + ChatColor.WHITE + 
                (daysAgo == 0 ? "Aujourd'hui" : "Il y a " + daysAgo + " jour(s)"));
        } else {
            player.sendMessage(ChatColor.YELLOW + "Dernier claim: " + ChatColor.GRAY + "Jamais");
        }
        
        player.sendMessage(ChatColor.YELLOW + "Statut: " + status);
        player.sendMessage(ChatColor.YELLOW + "Prochaine récompense: " + ChatColor.GOLD + nextReward + " coins");
        player.sendMessage("");
        
        // Milestones
        int nextMilestone = ((streak / 7) + 1) * 7;
        player.sendMessage(ChatColor.GRAY + "Prochain palier: " + nextMilestone + " jours");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Utilisez /daily preview pour voir les récompenses");
    }
}
