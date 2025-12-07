package fr.louis.practice.commands.admin;

import fr.louis.practice.PracticeCore;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public SetSpawnCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande est réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("practice.admin.setspawn")) {
            player.sendMessage("§cVous n'avez pas la permission d'utiliser cette commande.");
            return true;
        }
        
        plugin.setSpawnLocation(player.getLocation());
        player.sendMessage("§aLe spawn a été défini à votre position actuelle!");
        
        return true;
    }
}
