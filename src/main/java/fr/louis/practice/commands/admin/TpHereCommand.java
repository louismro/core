package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpHereCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.tphere")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /tphere <joueur>");
            return true;
        }
        
        Player staff = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return true;
        }
        
        var location = staff.getLocation();
        if (location != null) {
            target.teleport(location);
            sender.sendMessage(ChatColor.GREEN + "✓ " + target.getName() + " téléporté à vous!");
            target.sendMessage(ChatColor.YELLOW + "Vous avez été téléporté à " + staff.getName());
        }
        
        return true;
    }
}
