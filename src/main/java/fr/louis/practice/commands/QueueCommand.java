package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QueueCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public QueueCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande est réservée aux joueurs!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            player.sendMessage("§cUtilisation: /queue <join|leave> [queue]");
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "join":
                if (args.length < 2) {
                    player.sendMessage("§cUtilisation: /queue join <queue>");
                    listQueues(player);
                    return true;
                }
                
                String queueName = args[1];
                plugin.getQueueManager().addToQueue(player, queueName);
                break;
                
            case "leave":
                plugin.getQueueManager().removeFromQueue(player);
                break;
                
            default:
                player.sendMessage("§cUtilisation: /queue <join|leave> [queue]");
                break;
        }
        
        return true;
    }
    
    private void listQueues(Player player) {
        player.sendMessage("§eQueues disponibles:");
        for (fr.louis.practice.models.Queue queue : plugin.getQueueManager().getAllQueues()) {
            player.sendMessage("§7- §e" + queue.getName() + " §7(" + 
                queue.getDisplayName() + "§7)");
        }
    }
}
