package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public PayCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length < 2) {
            player.sendMessage("§cUsage: /pay <joueur> <montant>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§cJoueur introuvable.");
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage("§cVous ne pouvez pas vous payer vous-même.");
            return true;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage("§cMontant invalide.");
            return true;
        }
        
        if (amount <= 0) {
            player.sendMessage("§cLe montant doit être positif.");
            return true;
        }
        
        PracticePlayer senderPlayer = plugin.getPlayerManager().getOrCreate(player);
        PracticePlayer receiver = plugin.getPlayerManager().getOrCreate(target);
        
        if (senderPlayer.getCoins() < amount) {
            player.sendMessage("§cVous n'avez pas assez de coins. (Solde: §6" + senderPlayer.getCoins() + "§c)");
            return true;
        }
        
        // Transfer
        senderPlayer.setCoins(senderPlayer.getCoins() - amount);
        receiver.setCoins(receiver.getCoins() + amount);
        
        player.sendMessage("§a✓ Vous avez envoyé §6" + amount + " coins §aà §e" + target.getName());
        target.sendMessage("§a✓ Vous avez reçu §6" + amount + " coins §ade §e" + player.getName());
        
        return true;
    }
}
