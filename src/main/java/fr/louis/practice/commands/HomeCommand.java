package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public HomeCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            }
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            plugin.getHomeManager().teleportHome(player, "home");
            return true;
        }
        
        plugin.getHomeManager().teleportHome(player, args[0]);
        return true;
    }
}
