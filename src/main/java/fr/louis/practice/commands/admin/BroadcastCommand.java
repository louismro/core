package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class BroadcastCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.broadcast")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /broadcast <message>");
            return true;
        }
        
        String message = String.join(" ", args);
        
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.RED + "╔═════════════════════════════════╗");
        Bukkit.broadcastMessage(ChatColor.RED + "║ " + ChatColor.YELLOW + "★ ANNONCE" + ChatColor.RED + " ★");
        Bukkit.broadcastMessage(ChatColor.RED + "║");
        
        // Split long messages
        String[] words = message.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            if (line.length() + word.length() > 25) {
                Bukkit.broadcastMessage(ChatColor.RED + "║ " + ChatColor.WHITE + 
                    ChatColor.translateAlternateColorCodes('&', line.toString()));
                line = new StringBuilder();
            }
            line.append(word).append(" ");
        }
        
        if (line.length() > 0) {
            Bukkit.broadcastMessage(ChatColor.RED + "║ " + ChatColor.WHITE + 
                ChatColor.translateAlternateColorCodes('&', line.toString()));
        }
        
        Bukkit.broadcastMessage(ChatColor.RED + "╚═════════════════════════════════╝");
        Bukkit.broadcastMessage("");
        
        return true;
    }
}
