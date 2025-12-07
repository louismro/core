package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.tp")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /tp <joueur> [cible]");
            return true;
        }
        
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Spécifiez une cible!");
                return true;
            }
            
            Player player = (Player) sender;
            Player target = Bukkit.getPlayer(args[0]);
            
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
                return true;
            }
            
            var targetLoc = target.getLocation();
            if (targetLoc != null) {
                player.teleport(targetLoc);
                player.sendMessage(ChatColor.GREEN + "✓ Téléporté à " + target.getName());
            }
        } else {
            Player player = Bukkit.getPlayer(args[0]);
            Player target = Bukkit.getPlayer(args[1]);
            
            if (player == null || target == null) {
                sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
                return true;
            }
            
            var targetLoc = target.getLocation();
            if (targetLoc != null) {
                player.teleport(targetLoc);
                sender.sendMessage(ChatColor.GREEN + "✓ " + player.getName() + 
                    " téléporté à " + target.getName());
            }
        }
        
        return true;
    }
}
