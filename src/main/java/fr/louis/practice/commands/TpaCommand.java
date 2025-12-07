package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.TeleportRequest.TeleportType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TpaCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public TpaCommand(PracticeCore plugin) {
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
            player.sendMessage(ChatColor.RED + "Usage: /tpa <joueur>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage(ChatColor.RED + "Vous ne pouvez pas vous téléporter à vous-même!");
            return true;
        }
        
        plugin.getTeleportManager().sendTeleportRequest(player, target, TeleportType.TPA);
        
        return true;
    }
}
