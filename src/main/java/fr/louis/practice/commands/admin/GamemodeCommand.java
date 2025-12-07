package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.gamemode")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /gamemode <0|1|2|3> [joueur]");
            return true;
        }
        
        GameMode mode = parseGameMode(args[0]);
        if (mode == null) {
            sender.sendMessage(ChatColor.RED + "Mode invalide! Utilisez: 0, 1, 2, 3");
            return true;
        }
        
        Player target;
        if (args.length > 1) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Joueur introuvable!");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Spécifiez un joueur!");
                return true;
            }
            target = (Player) sender;
        }
        
        target.setGameMode(mode);
        target.sendMessage(ChatColor.GREEN + "✓ Mode de jeu changé: " + 
            ChatColor.YELLOW + mode.name());
        
        if (!target.equals(sender)) {
            sender.sendMessage(ChatColor.GREEN + "✓ Mode de " + target.getName() + 
                " changé: " + mode.name());
        }
        
        return true;
    }
    
    private GameMode parseGameMode(String input) {
        return switch (input.toLowerCase()) {
            case "0", "s", "survival" -> GameMode.SURVIVAL;
            case "1", "c", "creative" -> GameMode.CREATIVE;
            case "2", "a", "adventure" -> GameMode.ADVENTURE;
            case "3", "sp", "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };
    }
}
