package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PunishHistoryCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public PunishHistoryCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.staff.punish")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /punishhistory <joueur>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return true;
        }
        
        List<Punishment> history = plugin.getPunishmentManager().getPunishmentHistory(target.getUniqueId());
        
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "HISTORIQUE DE " + 
            target.getName().toUpperCase());
        sender.sendMessage(ChatColor.GOLD + "║                               ║");
        
        if (history.isEmpty()) {
            sender.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Aucune punition");
        } else {
            int shown = Math.min(history.size(), 10);
            for (int i = 0; i < shown; i++) {
                Punishment p = history.get(i);
                String status = p.isActive() ? ChatColor.RED + "●" : ChatColor.GRAY + "●";
                
                sender.sendMessage(ChatColor.GOLD + "║  " + status + " " + 
                    p.getType().getDisplayName());
                sender.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + p.getReason());
                
                if (p.getDurationMinutes() > 0) {
                    sender.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + 
                        "Durée: " + p.getFormattedDuration());
                }
            }
            
            if (history.size() > 10) {
                sender.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + 
                    "... et " + (history.size() - 10) + " autres");
            }
        }
        
        sender.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        sender.sendMessage("");
        
        return true;
    }
}
