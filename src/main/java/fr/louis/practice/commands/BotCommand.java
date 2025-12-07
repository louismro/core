package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PracticeBot.BotDifficulty;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BotCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public BotCommand(PracticeCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cCommande réservée aux joueurs.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            showBotMenu(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "spawn":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /bot spawn <easy|medium|hard|expert>");
                    return true;
                }
                
                try {
                    BotDifficulty difficulty = BotDifficulty.valueOf(args[1].toUpperCase());
                    
                    if (plugin.getBotManager().hasActiveBot(player.getUniqueId())) {
                        player.sendMessage("§cVous avez déjà un bot actif. Utilisez /bot remove");
                        return true;
                    }
                    
                    boolean success = plugin.getBotManager().spawnBot(
                        player, difficulty, player.getLocation().add(5, 0, 0), "NoDebuff");
                    
                    if (!success) {
                        player.sendMessage("§cImpossible de spawn le bot.");
                    }
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cDifficulté invalide: easy, medium, hard, expert");
                }
                break;
                
            case "remove":
            case "stop":
                if (!plugin.getBotManager().hasActiveBot(player.getUniqueId())) {
                    player.sendMessage("§cVous n'avez pas de bot actif.");
                    return true;
                }
                
                plugin.getBotManager().removeBot(player.getUniqueId());
                player.sendMessage("§a✓ Bot retiré");
                break;
                
            default:
                showBotMenu(player);
                break;
        }
        
        return true;
    }
    
    private void showBotMenu(Player player) {
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l    ENTRAÎNEMENT BOT");
        player.sendMessage("");
        player.sendMessage("§e/bot spawn <difficulté> §7- Spawner un bot");
        player.sendMessage("§e/bot remove §7- Retirer le bot");
        player.sendMessage("");
        player.sendMessage("§7Difficultés:");
        player.sendMessage(" §a▪ EASY §7- Débutant (50 XP)");
        player.sendMessage(" §e▪ MEDIUM §7- Intermédiaire (100 XP)");
        player.sendMessage(" §c▪ HARD §7- Avancé (200 XP)");
        player.sendMessage(" §4▪ EXPERT §7- Maître (400 XP)");
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
