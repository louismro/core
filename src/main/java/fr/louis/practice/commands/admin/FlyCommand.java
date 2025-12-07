package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandExecutor {
    
    public FlyCommand(PracticeCore plugin) {
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.fly")) {
            sender.sendMessage(ChatColor.RED + "Vous n'avez pas la permission!");
            return true;
        }
        
        Player target;
        if (args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
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
        
        boolean flying = !target.getAllowFlight();
        target.setAllowFlight(flying);
        target.setFlying(flying);
        
        String status = flying ? ChatColor.GREEN + "activé" : ChatColor.RED + "désactivé";
        target.sendMessage(ChatColor.YELLOW + "Mode vol " + status);
        
        if (!target.equals(sender)) {
            sender.sendMessage(ChatColor.GREEN + "✓ Vol " + status + ChatColor.GREEN + 
                " pour " + target.getName());
        }
        
        return true;
    }
}
