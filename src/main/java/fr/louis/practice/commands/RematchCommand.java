package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.managers.RematchManager.RematchRequest;
import fr.louis.practice.models.PlayerState;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RematchCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public RematchCommand(PracticeCore plugin) {
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
        
        if (args.length == 0 || !args[0].equalsIgnoreCase("accept")) {
            player.sendMessage("§cUtilisation: /rematch accept");
            return true;
        }
        
        // Vérifier qu'il a une demande
        if (!plugin.getRematchManager().hasRematchRequest(player.getUniqueId())) {
            player.sendMessage("§cVous n'avez aucune demande de rematch en attente.");
            return true;
        }
        
        // Vérifier le state
        if (practicePlayer.getState() != PlayerState.SPAWN) {
            player.sendMessage("§cVous devez être au spawn pour accepter un rematch!");
            return true;
        }
        
        RematchRequest request = plugin.getRematchManager().getRematchRequest(player.getUniqueId());
        Player senderPlayer = Bukkit.getPlayer(request.getSender());
        
        if (senderPlayer == null || !senderPlayer.isOnline()) {
            player.sendMessage("§cLe joueur qui vous a défié n'est plus en ligne.");
            plugin.getRematchManager().removeRematchRequest(player.getUniqueId());
            return true;
        }
        
        PracticePlayer senderPractice = plugin.getPlayerManager().getOrCreate(senderPlayer);
        if (senderPractice.getState() != PlayerState.SPAWN) {
            player.sendMessage("§cCe joueur n'est plus disponible.");
            plugin.getRematchManager().removeRematchRequest(player.getUniqueId());
            return true;
        }
        
        // Remove request
        plugin.getRematchManager().removeRematchRequest(player.getUniqueId());
        
        // Démarrer le duel
        if (request.isRanked()) {
            // Queue ranked
            plugin.getQueueManager().addToQueue(senderPlayer, request.getKitName(), true);
            plugin.getQueueManager().addToQueue(player, request.getKitName(), true);
        } else {
            // Duel direct - placeholder (createDuel not implemented)
            player.sendMessage("§cLa fonctionnalité de duel direct n'est pas encore implémentée.");
        }
        
        senderPlayer.sendMessage("§a" + player.getName() + " a accepté votre demande de rematch!");
        player.sendMessage("§aVous avez accepté le rematch!");
        
        return true;
    }
}
