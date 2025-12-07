package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MatchHistoryCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public MatchHistoryCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }
        Player target = args.length > 0 ? Bukkit.getPlayer(args[0]) : player;
        
        if (target == null) {
            player.sendMessage("§cJoueur introuvable.");
            return true;
        }
        
        var practicePlayer = plugin.getPlayerManager().getOrCreate(target);
        
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l  HISTORIQUE - " + target.getName());
        player.sendMessage("");
        player.sendMessage(" §7Victoires: §a" + practicePlayer.getWins());
        player.sendMessage(" §7Défaites: §c" + practicePlayer.getLosses());
        player.sendMessage(" §7W/L Ratio: §e" + String.format("%.2f", practicePlayer.getWinLossRatio()));
        player.sendMessage(" §7Killstreak: §6" + practicePlayer.getKillstreak());
        player.sendMessage(" §7Best Combo: §d" + plugin.getComboManager().getBestCombo(target.getUniqueId()));
        player.sendMessage("");
        player.sendMessage(" §7Derniers matchs: §8(Soon)");
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return true;
    }
}
