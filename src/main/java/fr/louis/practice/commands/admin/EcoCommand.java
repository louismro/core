package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcoCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public EcoCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.eco")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /eco <give|take|set> <joueur> <montant>");
            return true;
        }
        
        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayer(args[1]);
        
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }
        
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cMontant invalide.");
            return true;
        }
        
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(target);
        int oldBalance = practicePlayer.getCoins();
        
        switch (action) {
            case "give":
                practicePlayer.setCoins(oldBalance + amount);
                sender.sendMessage("§a✓ " + amount + " coins donnés à " + target.getName());
                target.sendMessage("§eVous avez reçu §6" + amount + " coins§e.");
                break;
                
            case "take":
                int newBalance = Math.max(0, oldBalance - amount);
                practicePlayer.setCoins(newBalance);
                sender.sendMessage("§a✓ " + amount + " coins retirés à " + target.getName());
                target.sendMessage("§c" + amount + " coins §eont été retirés de votre solde.");
                break;
                
            case "set":
                practicePlayer.setCoins(amount);
                sender.sendMessage("§a✓ Solde de " + target.getName() + " défini à " + amount + " coins");
                target.sendMessage("§eVotre solde a été défini à §6" + amount + " coins§e.");
                break;
                
            default:
                sender.sendMessage("§cAction invalide: give, take ou set");
                return true;
        }
        
        return true;
    }
}
