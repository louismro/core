package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.clear")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        Player target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Spécifiez un joueur!");
                return true;
            }
            target = (Player) sender;
        }
        
        target.getInventory().clear();
        target.getInventory().setArmorContents(null);
        target.sendMessage(ChatColor.GREEN + "✓ Inventaire vidé!");
        
        if (!target.equals(sender)) {
            sender.sendMessage(ChatColor.GREEN + "✓ Inventaire de " + target.getName() + " vidé!");
        }
        
        return true;
    }
}
