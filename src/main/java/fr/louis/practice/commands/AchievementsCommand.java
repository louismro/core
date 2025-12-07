package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Achievement;
import fr.louis.practice.models.Achievement.AchievementCategory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class AchievementsCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public AchievementsCommand(PracticeCore plugin) {
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
            showOverview(player);
            return true;
        }
        
        String categoryName = args[0].toUpperCase();
        try {
            AchievementCategory category = AchievementCategory.valueOf(categoryName);
            showCategory(player, category);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Catégorie invalide!");
            player.sendMessage(ChatColor.YELLOW + "Catégories disponibles: KILLS, WINS, STREAKS, ELO, SPECIAL, SOCIAL");
        }
        
        return true;
    }
    
    private void showOverview(Player player) {
        Set<String> completed = plugin.getAchievementManager().getCompletedAchievements(player.getUniqueId());
        int total = plugin.getAchievementManager().getAchievements().size();
        double percentage = plugin.getAchievementManager().getCompletionPercentage(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        player.sendMessage(ChatColor.YELLOW + "      ✦ VOS SUCCÈS ✦");
        player.sendMessage("");
        player.sendMessage(ChatColor.WHITE + "Progression: " + ChatColor.GREEN + completed.size() + 
            ChatColor.GRAY + "/" + ChatColor.WHITE + total + 
            ChatColor.GRAY + " (" + String.format("%.1f", percentage) + "%)");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Catégories:");
        
        for (AchievementCategory category : AchievementCategory.values()) {
            List<Achievement> achievements = plugin.getAchievementManager().getAchievementsByCategory(category);
            long categoryCompleted = achievements.stream()
                .filter(a -> completed.contains(a.getId()))
                .count();
            
            String color = categoryCompleted == achievements.size() ? 
                ChatColor.GREEN.toString() : ChatColor.YELLOW.toString();
            
            player.sendMessage(color + "  " + category.getDisplayName() + 
                ChatColor.GRAY + " (" + categoryCompleted + "/" + achievements.size() + ")");
        }
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Utilisez /achievements <catégorie> pour plus de détails");
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        player.sendMessage("");
    }
    
    private void showCategory(Player player, AchievementCategory category) {
        List<Achievement> achievements = plugin.getAchievementManager().getAchievementsByCategory(category);
        Set<String> completed = plugin.getAchievementManager().getCompletedAchievements(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ " + category.getDisplayName() + " ═══");
        player.sendMessage("");
        
        for (Achievement achievement : achievements) {
            boolean isCompleted = completed.contains(achievement.getId());
            int progress = plugin.getAchievementManager().getProgress(player.getUniqueId(), achievement.getId());
            
            String status;
            if (isCompleted) {
                status = ChatColor.GREEN + "✓ " + achievement.getName();
            } else {
                status = ChatColor.GRAY + "✗ " + ChatColor.WHITE + achievement.getName();
            }
            
            player.sendMessage(status);
            player.sendMessage(ChatColor.GRAY + "  " + achievement.getDescription());
            
            if (!isCompleted) {
                String progressBar = createProgressBar(progress, achievement.getRequirement());
                player.sendMessage(ChatColor.GRAY + "  Progression: " + progressBar + 
                    ChatColor.WHITE + " " + progress + "/" + achievement.getRequirement());
            }
            
            player.sendMessage(ChatColor.GOLD + "  Récompense: " + achievement.getCoinsReward() + " coins");
            
            if (achievement.getCosmeticReward() != null && !isCompleted) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "  + Cosmétique spécial!");
            }
            
            player.sendMessage("");
        }
    }
    
    private String createProgressBar(int current, int max) {
        int bars = 10;
        int filled = (int) ((double) current / max * bars);
        
        StringBuilder bar = new StringBuilder();
        bar.append(ChatColor.GREEN);
        
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                bar.append("█");
            } else if (i == filled) {
                bar.append(ChatColor.GRAY);
                bar.append("█");
            } else {
                bar.append("█");
            }
        }
        
        return bar.toString();
    }
}
