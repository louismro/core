package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

public class KillAllCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.killall")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        World world;
        if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else if (args.length > 0) {
            world = Bukkit.getWorld(args[0]);
            if (world == null) {
                sender.sendMessage("§cMonde introuvable.");
                return true;
            }
        } else {
            sender.sendMessage("§cSpécifiez un monde depuis la console.");
            return true;
        }
        
        int count = 0;
        
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Player) {
                continue; // Ne pas tuer les joueurs
            }
            
            // Kill monsters, animals, but not armor stands, item frames, etc.
            if (entity instanceof Monster || 
                entity instanceof Animals || 
                entity instanceof Slime ||
                entity instanceof Ghast ||
                entity instanceof EnderDragon ||
                entity instanceof Flying) {
                entity.remove();
                count++;
            }
        }
        
        sender.sendMessage("§a✓ " + count + " entités supprimées dans " + world.getName());
        
        return true;
    }
}
