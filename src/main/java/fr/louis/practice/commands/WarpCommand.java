package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Warp;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public WarpCommand(PracticeCore plugin) {
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
            showWarps(player);
            return true;
        }
        
        String warpName = args[0];
        plugin.getWarpManager().teleportToWarp(player, warpName);
        
        return true;
    }
    
    private void showWarps(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "WARPS DISPONIBLES" + ChatColor.GOLD + "       ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        if (plugin.getWarpManager().getWarps().isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Aucun warp");
        } else {
            for (Warp warp : plugin.getWarpManager().getWarps().values()) {
                if (warp.isPublicWarp() || player.hasPermission("practice.warp.admin")) {
                    player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + 
                        "• " + warp.getDisplayName());
                }
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "/warp <nom>");
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
}
