package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.weather")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /weather <clear|rain|thunder> [monde]");
            return true;
        }
        
        World world;
        if (sender instanceof Player && args.length == 1) {
            world = ((Player) sender).getWorld();
        } else if (args.length == 2) {
            world = Bukkit.getWorld(args[1]);
            if (world == null) {
                sender.sendMessage("§cMonde introuvable.");
                return true;
            }
        } else {
            sender.sendMessage("§cSpécifiez un monde depuis la console.");
            return true;
        }
        
        String weather = args[0].toLowerCase();
        
        switch (weather) {
            case "clear":
            case "sun":
                world.setStorm(false);
                world.setThundering(false);
                sender.sendMessage("§a✓ Météo définie sur §eBeau temps");
                break;
                
            case "rain":
            case "rainy":
                world.setStorm(true);
                world.setThundering(false);
                sender.sendMessage("§a✓ Météo définie sur §ePluie");
                break;
                
            case "thunder":
            case "storm":
                world.setStorm(true);
                world.setThundering(true);
                sender.sendMessage("§a✓ Météo définie sur §eOrage");
                break;
                
            default:
                sender.sendMessage("§cMétéo invalide: clear, rain ou thunder");
                return true;
        }
        
        return true;
    }
}
