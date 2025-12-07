package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerSettings;
import fr.louis.practice.models.PracticePlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public SettingsCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (sender != null) sender.sendMessage(ChatColor.RED + "Cette commande est réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        PracticePlayer practicePlayer = plugin.getPlayerManager().getOrCreate(player);
        PlayerSettings settings = practicePlayer.getSettings();
        
        if (args.length == 0) {
            displaySettings(player, settings);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "duels" -> {
                settings.setAllowDuels(!settings.isAllowDuels());
                player.sendMessage("§aDemandes de duels: " + 
                    (settings.isAllowDuels() ? "§aActivées" : "§cDésactivées"));
            }
            case "spectators" -> {
                settings.setAllowSpectators(!settings.isAllowSpectators());
                player.sendMessage("§aSpectateurs: " + 
                    (settings.isAllowSpectators() ? "§aAutorisés" : "§cInterdits"));
            }
            case "scoreboard" -> {
                settings.setShowScoreboard(!settings.isShowScoreboard());
                player.sendMessage("§aScoreboard: " + 
                    (settings.isShowScoreboard() ? "§aAffiché" : "§cMasqué"));
                
                // Appliquer immédiatement
                if (settings.isShowScoreboard()) {
                    plugin.getScoreboardManager().updateScoreboard(player);
                } else {
                    var scoreboardManager = plugin.getServer().getScoreboardManager();
                    if (scoreboardManager != null) {
                        player.setScoreboard(scoreboardManager.getNewScoreboard());
                    }
                }
            }
            default -> displaySettings(player, settings);
        }
        
        // Sauvegarder
        plugin.getMongoManager().savePlayer(practicePlayer);
        
        return true;
    }
    
    private void displaySettings(Player player, PlayerSettings settings) {
        player.sendMessage("§8§m                                    ");
        player.sendMessage("§6§lPARAMÈTRES");
        player.sendMessage("");
        player.sendMessage("§7Demandes de duels: " + 
            (settings.isAllowDuels() ? "§a✓ Activées" : "§c✗ Désactivées"));
        player.sendMessage("§7Spectateurs: " + 
            (settings.isAllowSpectators() ? "§a✓ Autorisés" : "§c✗ Interdits"));
        player.sendMessage("§7Scoreboard: " + 
            (settings.isShowScoreboard() ? "§a✓ Affiché" : "§c✗ Masqué"));
        player.sendMessage("");
        player.sendMessage("§7Utilisez §e/settings <option> §7pour modifier");
        player.sendMessage("§7Options: duels, spectators, scoreboard");
        player.sendMessage("§8§m                                    ");
    }
}
