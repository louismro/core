package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticePlayer;
import fr.louis.practice.models.SeasonalRank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SeasonCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public SeasonCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length > 0 && args[0].equalsIgnoreCase("ranks")) {
            showRanks(player);
            return true;
        }
        
        showSeasonInfo(player);
        return true;
    }
    
    private void showSeasonInfo(Player player) {
        PracticePlayer practicePlayer = plugin.getPlayerManager().getPlayer(player.getUniqueId());
        if (practicePlayer == null) return;
        
        int currentElo = practicePlayer.getGlobalElo();
        int peakElo = plugin.getSeasonManager().getPeakElo(player.getUniqueId());
        int seasonGain = plugin.getSeasonManager().getSeasonGain(player.getUniqueId());
        int daysLeft = plugin.getSeasonManager().getDaysUntilSeasonEnd();
        
        SeasonalRank currentRank = plugin.getSeasonManager().getRank(currentElo);
        SeasonalRank peakRank = plugin.getSeasonManager().getRank(peakElo);
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.YELLOW + "SAISON " + 
            plugin.getSeasonManager().getCurrentSeason() + ChatColor.GOLD + "                    ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Rang actuel: " + currentRank.getDisplayName());
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "ELO actuel: " + 
            ChatColor.YELLOW + currentElo);
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Peak rang: " + peakRank.getDisplayName());
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Peak ELO: " + 
            ChatColor.AQUA + peakElo);
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.WHITE + "Gain saison: " + 
            (seasonGain >= 0 ? ChatColor.GREEN + "+" : ChatColor.RED) + seasonGain);
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Fin dans: " + 
            ChatColor.WHITE + daysLeft + " jours");
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Utilisez /season ranks pour voir tous les rangs");
    }
    
    private void showRanks(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "═══ Rangs de Saison ═══");
        player.sendMessage("");
        
        for (SeasonalRank rank : plugin.getSeasonManager().getRanks()) {
            String eloRange = rank.getMinElo() + " - " + 
                (rank.getMaxElo() == -1 ? "∞" : String.valueOf(rank.getMaxElo()));
            
            player.sendMessage(rank.getFormattedPrefix() + " " + rank.getDisplayName());
            player.sendMessage(ChatColor.GRAY + "  ELO: " + ChatColor.WHITE + eloRange);
            player.sendMessage(ChatColor.GOLD + "  Récompense: " + rank.getSeasonCoinsReward() + " coins");
            player.sendMessage("");
        }
    }
}
