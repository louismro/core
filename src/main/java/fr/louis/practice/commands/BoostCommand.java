package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerBoost;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class BoostCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public BoostCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
            return true;
        }
        
        Player player = (Player) sender;
        showBoosts(player);
        return true;
    }
    
    private void showBoosts(Player player) {
        List<PlayerBoost> boosts = plugin.getBoostManager().getActiveBoosts(player.getUniqueId());
        
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "╔═══════════════════════════════╗");
        player.sendMessage(ChatColor.GOLD + "║   " + ChatColor.YELLOW + "VOS BOOSTS ACTIFS" + ChatColor.GOLD + "       ║");
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        
        if (boosts.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + "Aucun boost actif");
        } else {
            for (PlayerBoost boost : boosts) {
                player.sendMessage(ChatColor.GOLD + "║  " + boost.getType().getSymbol() + " " +
                    boost.getType().getDisplayName() + ChatColor.WHITE + " x" + 
                    boost.getMultiplier());
                player.sendMessage(ChatColor.GOLD + "║    " + ChatColor.GRAY + 
                    "Reste: " + ChatColor.WHITE + boost.getFormattedTimeRemaining());
            }
        }
        
        player.sendMessage(ChatColor.GOLD + "║                               ║");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + 
            "Obtenez des boosts via");
        player.sendMessage(ChatColor.GOLD + "║  " + ChatColor.GRAY + 
            "les crates et le shop!");
        player.sendMessage(ChatColor.GOLD + "╚═══════════════════════════════╝");
        player.sendMessage("");
    }
}
