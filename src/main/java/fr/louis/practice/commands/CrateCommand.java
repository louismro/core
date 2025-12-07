package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.CrateReward.Rarity;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CrateCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public CrateCommand(PracticeCore plugin) {
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
            showCrates(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        if (action.equals("open")) {
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Usage: /crate open <common|uncommon|rare|epic|legendary>");
                return true;
            }
            handleOpen(player, args[1]);
        } else {
            showCrates(player);
        }
        
        return true;
    }
    
    private void showCrates(Player player) {
        Map<Rarity, Integer> crates = plugin.getCrateManager().getPlayerCrates(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "VOS CRATES" + ChatColor.GOLD + "              ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        for (Rarity rarity : Rarity.values()) {
            int count = crates.getOrDefault(rarity, 0);
            player.sendMessage(ChatColor.GOLD + "║  " + rarity.getDisplayName() + 
                ChatColor.WHITE + ": " + ChatColor.YELLOW + count);
        }
        
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "/crate open <rareté>");
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
    
    private void handleOpen(Player player, String rarityName) {
        try {
            Rarity rarity = Rarity.valueOf(rarityName.toUpperCase());
            plugin.getCrateManager().openCrate(player, rarity);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Rareté invalide!");
            player.sendMessage(ChatColor.YELLOW + "Disponibles: common, uncommon, rare, epic, legendary");
        }
    }
}
