package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public CoinsCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Show own coins
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
                return true;
            }
            
            Player player = (Player) sender;
            PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
            
            player.sendMessage(ChatColor.GOLD + "═══ Vos Coins ═══");
            player.sendMessage(ChatColor.YELLOW + "Coins actuels: " + ChatColor.GREEN + practicePlayer.getCoins());
            player.sendMessage(ChatColor.YELLOW + "Total gagné: " + ChatColor.GREEN + practicePlayer.getTotalCoinsEarned());
            player.sendMessage(ChatColor.GRAY + "Utilisez /shop pour dépenser vos coins!");
            
            return true;
        }
        
        // Admin commands
        if (!sender.hasPermission("practice.coins.admin")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /coins <give|set|take> <joueur> <montant>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
            return true;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Montant invalide!");
            return true;
        }
        
        PracticePlayer targetPlayer = plugin.getPlayerManager().getPlayer(target.getUniqueId());
        
        switch (args[0].toLowerCase()) {
            case "give":
                targetPlayer.addCoins(amount);
                sender.sendMessage(ChatColor.GREEN + "Vous avez donné " + amount + " coins à " + target.getName());
                target.sendMessage(ChatColor.GREEN + "Vous avez reçu " + amount + " coins!");
                break;
                
            case "set":
                targetPlayer.setCoins(amount);
                sender.sendMessage(ChatColor.GREEN + "Coins de " + target.getName() + " définis à " + amount);
                target.sendMessage(ChatColor.GREEN + "Vos coins ont été définis à " + amount);
                break;
                
            case "take":
                if (targetPlayer.removeCoins(amount)) {
                    sender.sendMessage(ChatColor.GREEN + "Vous avez retiré " + amount + " coins à " + target.getName());
                    target.sendMessage(ChatColor.RED + "On vous a retiré " + amount + " coins!");
                } else {
                    sender.sendMessage(ChatColor.RED + target.getName() + " n'a pas assez de coins!");
                }
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "Action invalide! Utilisez: give, set, take");
                break;
        }
        
        return true;
    }
}
