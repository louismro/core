package fr.louis.practice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender != null) sender.sendMessage("§cCette commande est réservée aux joueurs!");
            return true;
        }
        
        Player player = (Player) sender;
        int ping = getPing(player);
        
        player.sendMessage("§aVotre ping: §e" + ping + "ms");
        
        return true;
    }
    
    private int getPing(Player player) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            return (int) handle.getClass().getField("ping").get(handle);
        } catch (NoSuchMethodException | NoSuchFieldException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            return -1;
        }
    }
}
