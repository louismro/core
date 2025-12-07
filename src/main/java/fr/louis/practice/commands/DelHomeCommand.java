package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DelHomeCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public DelHomeCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /delhome <nom>");
            return true;
        }
        
        plugin.getHomeManager().deleteHome(player, args[0]);
        return true;
    }
}
