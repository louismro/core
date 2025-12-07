package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.alert")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /alert <message>");
            return true;
        }
        
        // Build message
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        String finalMessage = message.toString().trim().replace("&", "§");
        
        // Send to all with permission
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("practice.alert.receive")) {
                player.sendMessage("");
                player.sendMessage("§c§l⚠ ALERTE STAFF ⚠");
                player.sendMessage("§7" + finalMessage);
                player.sendMessage("");
                var loc = player.getLocation();
                if (loc != null) {
                    player.playSound(loc, org.bukkit.Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
                }
            }
        }
        
        sender.sendMessage("§a✓ Alerte envoyée au staff");
        
        return true;
    }
}
