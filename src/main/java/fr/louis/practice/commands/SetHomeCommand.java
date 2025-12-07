package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHomeCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public SetHomeCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        String homeName = args.length > 0 ? args[0] : "home";
        
        plugin.getHomeManager().setHome(player, homeName, player.getLocation());
        return true;
    }
}
