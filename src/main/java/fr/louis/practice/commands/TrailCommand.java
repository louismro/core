package fr.louis.practice.commands;

import fr.louis.practice.PracticeCore;
import fr.louis.practice.models.PlayerTrail;
import fr.louis.practice.models.PlayerTrail.TrailType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TrailCommand implements CommandExecutor {
    private final PracticeCore plugin;
    
    public TrailCommand(PracticeCore plugin) {
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
            showTrailMenu(player);
            return true;
        }
        
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "toggle":
            case "on":
            case "off":
                plugin.getTrailManager().toggleTrail(player.getUniqueId());
                PlayerTrail trail = plugin.getTrailManager().getOrCreate(player.getUniqueId());
                player.sendMessage(trail.isEnabled() ? 
                    "§a✓ Traînée activée: " + trail.getType().getDisplayName() :
                    "§c✗ Traînée désactivée");
                break;
                
            case "set":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /trail set <type>");
                    return true;
                }
                
                try {
                    TrailType type = TrailType.valueOf(args[1].toUpperCase());
                    
                    if (!plugin.getTrailManager().getAvailableTrails(player).contains(type)) {
                        player.sendMessage("§cVous n'avez pas débloqué cette traînée.");
                        return true;
                    }
                    
                    plugin.getTrailManager().setTrailType(player.getUniqueId(), type);
                    player.sendMessage("§a✓ Traînée changée: " + type.getDisplayName());
                } catch (IllegalArgumentException e) {
                    player.sendMessage("§cType de traînée invalide.");
                }
                break;
                
            case "list":
                showAvailableTrails(player);
                break;
                
            default:
                showTrailMenu(player);
                break;
        }
        
        return true;
    }
    
    private void showTrailMenu(Player player) {
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l      TRAÎNÉES");
        player.sendMessage("");
        player.sendMessage("§e/trail toggle §7- Activer/désactiver");
        player.sendMessage("§e/trail set <type> §7- Changer le type");
        player.sendMessage("§e/trail list §7- Voir les traînées");
        player.sendMessage("");
        
        PlayerTrail trail = plugin.getTrailManager().getOrCreate(player.getUniqueId());
        player.sendMessage("§7Actuelle: " + trail.getType().getDisplayName());
        player.sendMessage("§7Statut: " + (trail.isEnabled() ? "§a✓ Activée" : "§c✗ Désactivée"));
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    private void showAvailableTrails(Player player) {
        List<TrailType> available = plugin.getTrailManager().getAvailableTrails(player);
        
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e§l   TRAÎNÉES DISPONIBLES");
        player.sendMessage("");
        
        for (TrailType type : available) {
            player.sendMessage(" §8▪ " + type.getDisplayName() + " §7(" + type.name().toLowerCase() + ")");
        }
        
        player.sendMessage("");
        player.sendMessage("§7Total: §e" + available.size() + " §7traînées");
        player.sendMessage("§6§l§m━━━━━━━━━━━━━━━━━━━━━━━━");
    }
}
