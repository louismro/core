package fr.louis.practice.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickAllCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("practice.kickall")) {
            sender.sendMessage("§cVous n'avez pas la permission.");
            return true;
        }
        
        String reason = "§cServeur en maintenance";
        if (args.length > 0) {
            StringBuilder reasonBuilder = new StringBuilder();
            for (String arg : args) {
                reasonBuilder.append(arg).append(" ");
            }
            reason = reasonBuilder.toString().trim();
        }
        
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("practice.kickall.bypass")) {
                player.kickPlayer(reason);
                count++;
            }
        }
        
        sender.sendMessage("§a✓ " + count + " joueurs ont été expulsés");
        
        return true;
    }
}
