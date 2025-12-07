package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.Match;
import fr.louis.practice.models.PlayerState;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectateCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public SpectateCommand(PracticeCore plugin) {
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
        
        if (args.length == 0) {
            player.sendMessage("§cUtilisation: /spectate <joueur>");
            return true;
        }
        
        // Vérifier le state
        if (practicePlayer.getState() != PlayerState.SPAWN) {
            player.sendMessage("§cVous devez être au spawn pour spectateur!");
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage("§cJoueur introuvable.");
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage("§cVous ne pouvez pas vous spectateur vous-même!");
            return true;
        }
        
        PracticePlayer targetPractice = plugin.getPlayerManager().getOrCreate(target);
        if (targetPractice.getState() != PlayerState.MATCH) {
            player.sendMessage("§cCe joueur n'est pas en match!");
            return true;
        }
        
        Match match = targetPractice.getCurrentMatch();
        if (match == null) {
            player.sendMessage("§cCe joueur n'est pas en match!");
            return true;
        }
        
        // Vérifier les settings du target
        if (!targetPractice.getSettings().isAllowSpectators()) {
            player.sendMessage("§cCe joueur n'autorise pas les spectateurs.");
            return true;
        }
        
        // Démarrer spectate
        plugin.getSpectatorManager().startSpectating(player, match);
        
        return true;
    }
}
