package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffModeCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public StaffModeCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender != null) sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }
        
        if (!sender.hasPermission("practice.staff")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Toggle staff mode
        if (plugin.getStaffModeManager().isInStaffMode(player.getUniqueId())) {
            plugin.getStaffModeManager().disableStaffMode(player);
        } else {
            plugin.getStaffModeManager().enableStaffMode(player);
        }
        
        return true;
    }
}
