package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public StatsCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCette commande est réservée aux joueurs!");
            return true;
        }
        
        Player player = (Player) sender;
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player);
        
        if (practicePlayer == null) {
            player.sendMessage("§cErreur lors du chargement de vos données!");
            return true;
        }
        
        displayStats(player, practicePlayer);
        
        return true;
    }
    
    private void displayStats(Player player, PracticePlayer practicePlayer) {
        player.sendMessage("§8§m---------------------------------");
        player.sendMessage("§b§lStatistiques de " + player.getName());
        player.sendMessage("");
        player.sendMessage("§7Killstreak actuel: §e" + practicePlayer.getCurrentStreak());
        player.sendMessage("§7Killstreak maximum: §e" + practicePlayer.getHighestStreak());
        player.sendMessage("");
        
        for (String kit : practicePlayer.getElo().keySet()) {
            int elo = practicePlayer.getElo(kit);
            PlayerStats stats = practicePlayer.getStats(kit);
            String rank = plugin.getEloManager().getRank(elo);
            String rankColor = plugin.getEloManager().getRankColor(elo);
            
            player.sendMessage("§e§l" + kit + ":");
            player.sendMessage("  §7ELO: §e" + elo + " " + rankColor + "[" + rank + "]");
            player.sendMessage("  §7Victoires: §a" + stats.getWins() + " §7Défaites: §c" + stats.getLosses());
            player.sendMessage("  §7K/D: §e" + String.format("%.2f", stats.getKDRatio()));
            player.sendMessage("  §7Winrate: §e" + String.format("%.1f%%", stats.getWinRate()));
            player.sendMessage("  §7Matchs joués: §e" + stats.getMatchesPlayed());
            player.sendMessage("");
        }
        
        player.sendMessage("§8§m---------------------------------");
    }
}
