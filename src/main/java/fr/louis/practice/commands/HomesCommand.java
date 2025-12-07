package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Home;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class HomesCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public HomesCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        List<Home> homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "VOS HOMES" + 
            ChatColor.GOLD + " (" + homes.size() + ")            ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        if (homes.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Aucun home défini");
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Utilisez /sethome");
        } else {
            for (Home home : homes) {
                player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "• " + home.getName());
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
        
        return true;
    }
}
