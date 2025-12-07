package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.DailyQuest;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuestsCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public QuestsCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            }
            return true;
        }
        List<DailyQuest> quests = plugin.getQuestManager().getPlayerQuests(player.getUniqueId());
        
        player.sendMessage(ChatColor.GOLD + "═══════ Quêtes Journalières ═══════");
        
        for (int i = 0; i < quests.size(); i++) {
            DailyQuest quest = quests.get(i);
            
            ChatColor statusColor = quest.isCompleted() ? ChatColor.GREEN : 
                                   quest.isExpired() ? ChatColor.GRAY : ChatColor.YELLOW;
            String status = quest.isCompleted() ? "✓ Complétée" : 
                           quest.isExpired() ? "✗ Expirée" : 
                           quest.getCurrentProgress() + "/" + quest.getTargetAmount();
            
            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Quête " + (i + 1) + ": " + 
                ChatColor.WHITE + quest.getFormattedDescription());
            player.sendMessage(ChatColor.GRAY + "Progression: " + statusColor + status);
            
            if (!quest.isCompleted() && !quest.isExpired()) {
                // Progress bar
                int barLength = 20;
                int filledBars = (int) (quest.getProgressPercentage() / 100.0 * barLength);
                StringBuilder bar = new StringBuilder(ChatColor.GREEN + "[");
                for (int j = 0; j < barLength; j++) {
                    if (j < filledBars) {
                        bar.append("█");
                    } else {
                        bar.append(ChatColor.GRAY).append("█");
                    }
                }
                bar.append(ChatColor.GREEN).append("] ").append(ChatColor.YELLOW)
                    .append(String.format("%.1f%%", quest.getProgressPercentage()));
                player.sendMessage(bar.toString());
            }
            
            player.sendMessage(ChatColor.GOLD + "Récompense: " + ChatColor.YELLOW + quest.getRewardCoins() + " coins");
            
            if (!quest.isCompleted() && !quest.isExpired()) {
                long timeLeft = quest.getExpiresAt() - System.currentTimeMillis();
                long hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60;
                player.sendMessage(ChatColor.GRAY + "Expire dans: " + hours + "h " + minutes + "m");
            }
        }
        
        player.sendMessage("");
        int completed = plugin.getQuestManager().getTotalCompletedQuests(player.getUniqueId());
        player.sendMessage(ChatColor.GREEN + "Quêtes complétées aujourd'hui: " + ChatColor.YELLOW + completed);
        player.sendMessage(ChatColor.GOLD + "═══════════════════════════════");
        
        return true;
    }
}
