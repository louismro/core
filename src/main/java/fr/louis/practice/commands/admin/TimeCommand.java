package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.time")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /time <set|add> <valeur> [monde]");
            sender.sendMessage("§cOu: /time <day|night|noon|midnight>");
            return true;
        }
        
        World world;
        if (sender instanceof Player && args.length < 3) {
            world = ((Player) sender).getWorld();
        } else if (args.length == 3) {
            world = Bukkit.getWorld(args[2]);
            if (world == null) {
                sender.sendMessage("§cMonde introuvable.");
                return true;
            }
        } else {
            sender.sendMessage("§cSpécifiez un monde depuis la console.");
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "day":
                world.setTime(1000);
                sender.sendMessage("§a✓ Temps défini sur §eJour");
                break;
                
            case "night":
                world.setTime(13000);
                sender.sendMessage("§a✓ Temps défini sur §eNuit");
                break;
                
            case "noon":
                world.setTime(6000);
                sender.sendMessage("§a✓ Temps défini sur §eMidi");
                break;
                
            case "midnight":
                world.setTime(18000);
                sender.sendMessage("§a✓ Temps défini sur §eMinuit");
                break;
                
            case "set":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /time set <valeur>");
                    return true;
                }
                
                try {
                    long time = Long.parseLong(args[1]);
                    world.setTime(time);
                    sender.sendMessage("§a✓ Temps défini à §e" + time);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cValeur invalide.");
                }
                break;
                
            case "add":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /time add <valeur>");
                    return true;
                }
                
                try {
                    long amount = Long.parseLong(args[1]);
                    world.setTime(world.getTime() + amount);
                    sender.sendMessage("§a✓ " + amount + " ajouté au temps");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cValeur invalide.");
                }
                break;
                
            default:
                sender.sendMessage("§cAction invalide.");
                return true;
        }
        
        return true;
    }
}
