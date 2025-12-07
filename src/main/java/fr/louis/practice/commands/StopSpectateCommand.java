package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerState;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StopSpectateCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public StopSpectateCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Cette commande est réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        
        if (practicePlayer.getState() != PlayerState.SPECTATING) {
            player.sendMessage("§cVous ne spectateur pas actuellement!");
            return true;
        }
        
        plugin.getSpectatorManager().stopSpectating(player);
        
        return true;
    }
}
