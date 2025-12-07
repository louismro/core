package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.staffchat")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /sc <message>");
            return true;
        }
        
        // Build message
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        
        String senderName = sender instanceof Player ? ((Player) sender).getName() : "Console";
        String finalMessage = "§c[STAFF] §e" + senderName + " §7» §f" + message.toString().trim();
        
        // Send to all staff
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("practice.staffchat")) {
                player.sendMessage(finalMessage);
            }
        }
        
        // Send to console
        Bukkit.getConsoleSender().sendMessage(finalMessage);
        
        return true;
    }
}
