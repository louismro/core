package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DuelCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public DuelCommand(PracticeCore plugin) {
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
            player.sendMessage("§cUtilisation: /duel <joueur> [kit]");
            player.sendMessage("§7ou /duel accept [joueur]");
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        if (subCommand.equals("accept")) {
            plugin.getDuelManager().acceptDuel(player);
            return true;
        }
        
        if (subCommand.equals("deny")) {
            plugin.getDuelManager().denyDuel(player);
            return true;
        }
        
        // Sinon c'est un nom de joueur
        Player target = Bukkit.getPlayer(args[0]);
        
        if (target == null) {
            player.sendMessage("§cJoueur introuvable!");
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage("§cVous ne pouvez pas vous défier vous-même!");
            return true;
        }
        
        String kitName = args.length > 1 ? args[1] : "NoDebuff";
        
        plugin.getDuelManager().sendDuelRequest(player, target, kitName);
        
        return true;
    }
}
