package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerProfile;
import fr.louis.practice.models.PracticePlayer;
import fr.louis.practice.models.SeasonalRank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;

public class ProfileCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public ProfileCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        
        Player target = player;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Joueur introuvable!");
                return true;
            }
        }
        
        showProfile(player, target);
        return true;
    }
    
    private void showProfile(Player viewer, Player target) {
        PlayerProfile profile = plugin.getProfileManager().getProfile(target.getUniqueId());
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(target.getUniqueId());
        
        if (practicePlayer == null) {
            viewer.sendMessage(ChatColor.RED + "Profil introuvable!");
            return;
        }
        
        SeasonalRank rank = plugin.getSeasonManager().getRank(practicePlayer.getGlobalElo());
        
        viewer.sendMessage("");
        viewer.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "PROFIL DE " + target.getName().toUpperCase());
        viewer.sendMessage(ChatColor.GOLD + "║                               ║");
        
        // Rang et ELO
        viewer.sendMessage(ChatColor.GOLD + "║  " + rank.getFormattedPrefix() + " " + 
            ChatColor.GRAY + "ELO: " + ChatColor.WHITE + practicePlayer.getGlobalElo());
        
        // Stats principales
        viewer.sendMessage(ChatColor.GOLD + "║                               ║");
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "Statistiques:");
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Kills: " + 
            ChatColor.GREEN + profile.getTotalKills() + ChatColor.GRAY + " / " + 
            ChatColor.RED + profile.getTotalDeaths() + 
            ChatColor.GRAY + " (K/D: " + String.format("%.2f", profile.getKDRatio()) + ")");
        
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Matchs: " + 
            ChatColor.GREEN + profile.getTotalWins() + ChatColor.GRAY + "/" + 
            ChatColor.RED + profile.getTotalLosses() + 
            ChatColor.GRAY + " (" + String.format("%.1f", profile.getWinRate()) + "%)");
        
        // Streaks
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Best Streak: " + 
            ChatColor.AQUA + profile.getBestKillStreak() + " kills");
        
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Best Win Streak: " + 
            ChatColor.GOLD + profile.getBestWinStreak());
        
        // Saison
        viewer.sendMessage(ChatColor.GOLD + "║                               ║");
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "Saison " + 
            plugin.getSeasonManager().getCurrentSeason() + ":");
        
        int peakElo = plugin.getSeasonManager().getPeakElo(target.getUniqueId());
        int gain = plugin.getSeasonManager().getSeasonGain(target.getUniqueId());
        
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Peak: " + 
            ChatColor.AQUA + peakElo + ChatColor.GRAY + " (+" + gain + ")");
        
        // Économie
        viewer.sendMessage(ChatColor.GOLD + "║                               ║");
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GOLD + "Coins: " + 
            practicePlayer.getCoins() + ChatColor.GRAY + " (Total: " + 
            profile.getTotalCoinsEarned() + ")");
        
        // Achievements
        int achCompleted = plugin.getAchievementManager()
            .getCompletedAchievements(target.getUniqueId()).size();
        int achTotal = plugin.getAchievementManager().getAchievements().size();
        
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Succès: " + 
            ChatColor.YELLOW + achCompleted + ChatColor.GRAY + "/" + achTotal);
        
        // Temps de jeu
        viewer.sendMessage(ChatColor.GOLD + "║                               ║");
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Temps de jeu: " + 
            ChatColor.GRAY + profile.getFormattedPlaytime());
        
        // Membre depuis
        Duration since = Duration.between(profile.getFirstJoin(), LocalDateTime.now());
        long days = since.toDays();
        viewer.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Membre: " + 
            ChatColor.GRAY + days + " jours");
        
        viewer.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        viewer.sendMessage("");
    }
}
