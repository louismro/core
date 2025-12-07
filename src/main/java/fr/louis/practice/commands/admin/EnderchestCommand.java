package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderchestCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.enderchest")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            // Open own enderchest
            player.openInventory(player.getEnderChest());
            player.sendMessage("§eOuverture de votre enderchest...");
        } else {
            // Open another player's enderchest
            if (!sender.hasPermission("practice.enderchest.others")) {
                sender.sendMessage("§cVous n'avez pas la permission.");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§cJoueur introuvable.");
                return true;
            }
            
            player.openInventory(target.getEnderChest());
            player.sendMessage("§eOuverture de l'enderchest de §6" + target.getName() + "§e...");
        }
        
        return true;
    }
}
