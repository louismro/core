package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public VanishCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.vanish")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Toggle vanish
        boolean vanished = plugin.getSpectatorManager().isVanished(player.getUniqueId());
        
        if (vanished) {
            // Unvanish
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.showPlayer(plugin, player);
            }
            plugin.getSpectatorManager().removeVanish(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "Mode vanish " + ChatColor.RED + "désactivé");
        } else {
            // Vanish
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasPermission("practice.vanish")) {
                    online.hidePlayer(plugin, player);
                }
            }
            plugin.getSpectatorManager().addVanish(player.getUniqueId());
            player.sendMessage(ChatColor.YELLOW + "Mode vanish " + ChatColor.GREEN + "activé");
        }
        
        return true;
    }
}
