package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpAllCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.tpall")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player staff = (Player) sender;
        int count = 0;
        var location = staff.getLocation();
        
        if (location != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(staff)) {
                    player.teleport(location);
                    count++;
                }
            }
            
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Tous les joueurs ont été téléportés à " + 
                staff.getName() + "!");
            sender.sendMessage(ChatColor.GREEN + "✓ " + count + " joueurs téléportés!");
        }
        
        return true;
    }
}
