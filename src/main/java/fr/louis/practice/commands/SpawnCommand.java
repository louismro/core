package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public SpawnCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande est réservée aux joueurs!");
            return true;
        }
        
        Player player = (Player) sender;
        
        player.teleport(plugin.getSpawnLocation());
        player.sendMessage("§aTéléportation au spawn...");
        
        return true;
    }
}
