package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.sudo")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /sudo <joueur> <commande>");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§cJoueur introuvable.");
            return true;
        }
        
        // Reconstruct command
        StringBuilder commandBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            commandBuilder.append(args[i]);
            if (i < args.length - 1) {
                commandBuilder.append(" ");
            }
        }
        
        String commandToRun = commandBuilder.toString();
        
        // Execute command as player
        boolean success = target.performCommand(commandToRun);
        
        if (success) {
            sender.sendMessage("§a✓ " + target.getName() + " a exécuté: §f/" + commandToRun);
        } else {
            sender.sendMessage("§cÉchec de l'exécution de la commande.");
        }
        
        return true;
    }
}
