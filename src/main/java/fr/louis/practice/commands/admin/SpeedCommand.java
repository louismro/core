package fr.louis.practice.commands.admin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.speed")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /speed <1-10>");
            return true;
        }
        
        try {
            int speed = Integer.parseInt(args[0]);
            
            if (speed < 1 || speed > 10) {
                sender.sendMessage(ChatColor.RED + "La vitesse doit être entre 1 et 10!");
                return true;
            }
            
            float speedValue = speed / 10.0f;
            
            if (player.isFlying()) {
                player.setFlySpeed(speedValue);
                player.sendMessage(ChatColor.GREEN + "✓ Vitesse de vol: " + 
                    ChatColor.YELLOW + speed);
            } else {
                player.setWalkSpeed(speedValue);
                player.sendMessage(ChatColor.GREEN + "✓ Vitesse de marche: " + 
                    ChatColor.YELLOW + speed);
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Nombre invalide!");
        }
        
        return true;
    }
}
