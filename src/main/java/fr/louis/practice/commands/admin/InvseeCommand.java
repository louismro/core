package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InvseeCommand implements CommandExecutor {
    
    public InvseeCommand(PracticeCore plugin) {
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.invsee")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /invsee <joueur>");
            return true;
        }
        
        Player viewer = (Player) sender;
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return true;
        }
        
        viewer.openInventory(target.getInventory());
        viewer.sendMessage(ChatColor.GREEN + "✓ Inventaire de " + target.getName() + " ouvert!");
        
        return true;
    }
}
